package jbash.environment;

import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.File;

public class FileBuffer {
    File file;
    int offset;
    String mode;
    String buffer;

    FileBuffer(String filepath, String mode) {
        this.file = FileSystemAPI.getInstance().getFileSystemFile(filepath).orElse(null);
        this.mode = mode;
        this.offset = 0;
        this.buffer = "";
    }
}
