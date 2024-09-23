package jbash.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Directory extends FileSystemObject {
    private final List<FileSystemObject> children = new ArrayList<>();

    public Directory(String name, Directory parent) {
        super(name, parent);
    }

    public void addChild(FileSystemObject fileSystemObject) {
        children.add(fileSystemObject);
    }

    public void removeChild(FileSystemObject file) {
        children.remove(file);
    }

    public Optional<FileSystemObject> findChild(String name) {
        return children.stream().filter(FSO -> FSO.getName().equals(name)).findFirst();
    }

    public List<FileSystemObject> getChildren() {
        return this.children;
    }
}
