package jbash.filesystem;

import java.io.File;
import java.io.*;

/**
 * Abstraction over The Big File (aka a disk stored on our Host OS.)
 */
public class Disk {
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
    Disk(String name, long sizeBytes) {
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
        fs.writeBytes("JFS!");  // MUST BE AT MOST 4 BYTES

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
}
