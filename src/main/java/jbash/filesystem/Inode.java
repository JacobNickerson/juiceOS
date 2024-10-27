package jbash.filesystem;


enum FileType {
    File,
    Directory
}

record Permission(boolean r, boolean w, boolean x) {};
record FilePerms(Permission owner, Permission group, Permission other) {}


public class Inode {
    FileType filetype;
    FilePerms perms;
    int userId;
    int groupId;
    int numRefs;
    long tsCreate;
    long tsModify;
    long tsAccess;
    long[] dataPtrs;
    long inodePtr;

    Inode(FileType filetype) {
        this(filetype, parsePerms("rw-r--r--"));
    }

    Inode(FileType filetype, FilePerms permissions) {
        this.filetype = filetype;
        this.perms = permissions;
        this.userId = 0;
        this.groupId = 0;
        this.numRefs = 1;
        this.tsCreate = 0;
        this.tsModify = 0;
        this.tsAccess = 0;
        dataPtrs = new long[12];
        inodePtr = TBFF.writeInodeToDisk(this);
    }

    /**
     * Given a permstring in the format "rwxrwxrwx", returns a FilePerms record.
     * Bad and hacky but it works
     */
    static FilePerms parsePerms(String permString) {
        if (permString.length() != 9) throw new RuntimeException("Invalid permission string: Expected 9 characters matching regex [r|w|x|-]");
        return new FilePerms(
                new Permission(permString.charAt(0) != '-', permString.charAt(1) != '-',permString.charAt(2) != '-'),
                new Permission(permString.charAt(3) != '-', permString.charAt(4) != '-',permString.charAt(5) != '-'),
                new Permission(permString.charAt(6) != '-', permString.charAt(7) != '-',permString.charAt(8) != '-')
        );
    }

    int getFileType() {
        return (this.filetype == FileType.File) ? 1 : 2;
    }

    FilePerms getPermissions() {
        return perms;
    }

    int getUserId() {
        return userId;
    }

    int getGroupId() {
        return groupId;
    }

    int getNumRefs() {
        return numRefs;
    }

    long getCreateTime() {
        return tsCreate;
    }

    long getModTime() {
        return tsModify;
    }

    long getAccessTime() {
        return tsAccess;
    }

    long[] getDataPtrs() {
        return dataPtrs;
    }
}
