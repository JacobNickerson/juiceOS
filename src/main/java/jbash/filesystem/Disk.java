package jbash.filesystem;

import java.io.File;
import java.io.*;

/**
 * Abstraction over The Big File (aka a disk stored on our Host OS.)
 */
public class Disk {
    private final RandomAccessFile fs;

    // Offsets for retrieving from the file
    private static final int OFFSET_MAGIC_NUMBER     = 0;
    private static final int OFFSET_NUM_INODES       = 4;
    private static final int OFFSET_INODE_SIZE       = 12;
    private static final int OFFSET_INODES_START_PTR = 20;
    private static final int OFFSET_DATA_START_PTR   = 24;

    /**
     * Constructor for a new disk.
     * Will search for an existing file system under <code>name</code>,
     * and will create it with size <code>sizeBytes</code> if it doesn't exist.
     * @param name
     * @param sizeBytes
     */
    Disk(String name, long sizeBytes) {
        // Minimum size for the file system is 1MB
        if (sizeBytes < 1024*1024) {
            System.out.println("WARNING: Cannot create filesystem of size "+sizeBytes);
            System.out.println("         The system will not be recreated.");
        }
        try {
            fs = new RandomAccessFile(name, "rw");

            // Assume already initialized
            if (fs.length() == sizeBytes) {
               return;
            }

            // Filesystem by this name already exists, but isn't the size we specified!
            else if (fs.length() != 0 && fs.length() != sizeBytes) {
                System.out.println("WARNING: Filesystem "+name+" already exists, but is of size "+fs.length());
                System.out.println("         The system will not be recreated.");
                return;
            }

            // Otherwise, let's format it.
            System.out.print("Formatting filesystem "+name+"...");

            format(sizeBytes);
            System.out.println("done.");


        } catch (Exception e) {
            // Vague error but I doubt this would happen often
            throw new RuntimeException("Cannot create disk: "+name);
        }
    }

    /**
     * Retrieves the number of inodes in this filesystem.
     * @return
     */
    long getNumINodes() throws IOException {
        fs.seek(4);
        fs.readLong();
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

        // Magic number at the start to tell this is a jfs file
        fs.seek(OFFSET_MAGIC_NUMBER);
        fs.writeBytes("JFS!");  // MUST BE 4 BYTES

        // Default ratio for this small filesystem is 1 inode per 8kb
        long numINodes = sizeBytes / (8 * 1024);
        fs.seek(OFFSET_NUM_INODES);
        fs.writeLong(numINodes);

        // Default size is 256, that should be reasonable, right?
        long inodeSizeBytes = 256;
        fs.seek(OFFSET_INODE_SIZE);
        fs.writeLong(inodeSizeBytes);
    }
}
