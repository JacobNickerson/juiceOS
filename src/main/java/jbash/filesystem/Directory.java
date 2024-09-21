package jbash.filesystem;

import java.util.List;

public class Directory extends FileSystemObject {
    private List<FileSystemObject> children;
    private Directory parent;

    public Directory(String name) {
        super(name);
    }
}
