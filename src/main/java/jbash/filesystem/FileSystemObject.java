package jbash.filesystem;

import java.nio.file.FileSystem;

public abstract class FileSystemObject {
    private final String name;

    public FileSystemObject(String name) {
        this.name = name;
    }
}
