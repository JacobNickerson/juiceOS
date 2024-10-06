package jbash.filesystem;

public class Inode {
    public int inodeNumber;                          // unique inode number
    public volatile int referenceCount;              // number of hardlinks lead to this file
    public int userId;                               // user id of owner
    public int groupId;                              // group id of owner
    public long fileSizeBytes;                       // file size in bytes
    public long fileSizeBlocks;                      // file size in blocks
}
