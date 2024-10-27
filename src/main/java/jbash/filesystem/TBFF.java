package jbash.filesystem;

import java.io.*;

/**
 * Abstraction over The Big File (aka a disk stored on our Host OS.)
 */
public class TBFF {
    private final RandomAccessFile fs;
    private final long SIZE_BYTES;
    private static final int BLOCK_SIZE = 1024 * 4;  // block size of 4K is probably good?
    private final int INODE_SIZE_BYTES;
    private final int NUM_INODES;

    // Offsets for retrieving from the file
    private static final int OFFSET_MAGIC_NUMBER     =                        0;
    private static final int OFFSET_NUM_INODES       = OFFSET_MAGIC_NUMBER   +4;
    private static final int OFFSET_INODE_SIZE       = OFFSET_NUM_INODES     +4;
    private static final int OFFSET_DATA_START_PTR   = OFFSET_INODE_SIZE     +4;
    private static final int OFFSET_INODES_START     = OFFSET_DATA_START_PTR +8;

    /**
     * Constructor for a new disk.
     * Will search for an existing disk under <code>name</code>,
     * and will create it with size <code>sizeBytes</code> if it doesn't exist.
     * @param name filename of the existing disk, or new one
     * @param sizeBytes size, in bytes, of the disk
     */
    public TBFF(String name, long sizeBytes) {
        // Minimum size for the file system is 1MB
        if (sizeBytes < 1024*1024) {
            System.out.println("WARNING: Cannot create filesystem of size "+sizeBytes);
            System.out.println("         The system will not be recreated.");
        }
        try {
            fs = new RandomAccessFile(name, "rw");

            // Filesystem by this name already exists, but isn't the size we specified!
            if (fs.length() != 0 && fs.length() != sizeBytes) {
                System.out.println("WARNING: Filesystem " + name + " already exists, but is of size " + fs.length());
                System.out.println("         The system will not be recreated.");
            }

            // Filesystem doesn't yet exist, let's format one.
            else if (fs.length() == 0){
                System.out.print("Formatting filesystem "+name+"...");
                format(sizeBytes);
                System.out.println("done.");
            }

            System.out.println("Loading filesystem "+name+";");

            // Set important static variables.
            SIZE_BYTES = fs.length();

            fs.seek(OFFSET_INODE_SIZE);
            INODE_SIZE_BYTES = fs.readInt();

            fs.seek(OFFSET_NUM_INODES);
            NUM_INODES = fs.readInt();

        } catch (Exception e) {
            // Vague error but I doubt this would happen often
            throw new RuntimeException("Cannot create disk: "+name);
        }
    }

    /**
     * Formats and resets the file system, clearing it entirely.
     * Use with caution!
     */
    void format(long sizeBytes) throws IOException {
        fs.setLength(sizeBytes);

        // Creates file of sizeBytes filled with zeroes
        byte[] zeros = new byte[1024];
        long remainingBytes = sizeBytes;
        while (remainingBytes > 0) {
            int toWrite = (int) Math.min(zeros.length, remainingBytes);
            fs.write(zeros, 0, toWrite);
            remainingBytes -= toWrite;
        }

        // 4 BYTES: Magic number at the start to tell this is a jfs file
        fs.seek(OFFSET_MAGIC_NUMBER);
        fs.writeBytes("TBFF");  // MUST BE AT MOST 4 BYTES

        // 4 BYTES:
        int numINodes = (int) (SIZE_BYTES / (8 * 1024));
        fs.seek(OFFSET_NUM_INODES);
        fs.writeInt(numINodes);

        // 4 BYTES:
        int inodeSizeBytes = 256;
        fs.seek(OFFSET_INODE_SIZE);
        fs.writeInt(inodeSizeBytes);

        // 8 BYTES: Pointer to the data block.
        long dataBlockPtr = (long) numINodes * inodeSizeBytes;
        fs.seek(OFFSET_DATA_START_PTR);
        fs.writeLong(dataBlockPtr);
    }

    /**
     * Iterates through the inode block of TBFF and finds a spot
     * @return Offset (pointer) to the spot in TBFF where a new inode may be placed.
     */
    long getFreeInodeSpot() throws IOException {
        // Linear search the first byte of each possible inode, looking for a 0 where file type is stored
        for (long inodeStartPos = OFFSET_INODES_START; inodeStartPos < (long) OFFSET_INODES_START + (long) NUM_INODES * INODE_SIZE_BYTES; inodeStartPos += INODE_SIZE_BYTES) {
            fs.seek(inodeStartPos);
            if ((fs.readByte() & 0xFF) == 0) {  // Found an empty byte 0 in an inode
                return inodeStartPos;
            }
        }

        return -1;
    }

    /**
     * Given a new inode, writes it to TBFF and returns its offset.
     * If no space is available, throws a runtime exception.
     * @param inode Inode to write to disk.
     * @return Offset (pointer) to this inode in TBFF.
     */
    public int writeInodeToDisk(Inode inode) throws RuntimeException, IOException {
        long inodePtr = getFreeInodeSpot();

        // TODO:
        //   Once we've obtained the free spot, you'll want to write it to TBFF
        //   using a similar method to ``format()``. Write the inode to disk
        //   however you'd like, but ensure it doesn't go over INODE_SIZE_BYTES.
        //   We may also consider making the first byte of the inode something like
        //   "F" for files or "D" for directories, so getFreeInodeSpot() only needs
        //   to check one byte to see if a spot is free.

        // Writing file type
        fs.seek(inodePtr);
        fs.writeInt(inode.getFileType());

        // Writing permissions
        FilePerms inodePerms = inode.getPermissions();

        Permission owner = inodePerms.owner();
        byte ownerPerms = 0;
        if (owner.r()) { ownerPerms |= 0b00000100; }
        if (owner.w()) { ownerPerms |= 0b00000010; }
        if (owner.x()) { ownerPerms |= 0b00000001; }

        Permission group = inodePerms.group();
        byte groupPerms = 0;
        if (group.r()) { groupPerms |= 0b00000100; }
        if (group.w()) { groupPerms |= 0b00000010; }
        if (group.x()) { groupPerms |= 0b00000001; }

        Permission other = inodePerms.group();
        byte otherPerms = 0;
        if (other.r()) { otherPerms |= 0b00000100; }
        if (other.w()) { otherPerms |= 0b00000010; }
        if (other.x()) { otherPerms |= 0b00000001; }

        fs.writeByte(ownerPerms);
        fs.writeByte(groupPerms);
        fs.writeByte(otherPerms);

        // Writing UID, GUID, etc.
        fs.writeInt(inode.getUserId());
        fs.writeInt(inode.getGroupId());
        fs.writeInt(inode.getNumRefs());
        fs.writeLong(inode.getCreateTime());
        fs.writeLong(inode.getModTime());
        fs.writeLong(inode.getAccessTime());

        // Writing pointers to data block
        long[] dataPtrs = inode.getDataPtrs();
        for (long dataPtr : dataPtrs) {
            if (dataPtr == 0) { break; }
            fs.writeLong(dataPtr);
        }

        return 0;
    }

    /**
     * Given a long offset, reads the inode stored at that position in TBFF
     * and returns an inode object constructed from it.
     * If no inode is found at that position, will raise some typa error idk
     * @param offset Position in TBFF where the Inode is found
     * @return newInode created from reading TBFF
     */
    public Inode readInodeFromDisk(long offset) {
        Inode newInode = new Inode(FileType.File); // placeholder
        return newInode;
    }
}
