package jbash.filesystem;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileSystemAPI {
    private final Directory root;
    private Directory currentDirectory;

    public FileSystemAPI() {
        this.root = new Directory("root", null);
        this.currentDirectory = this.root;
    }

    // TODO: Add support for quotes
    public Optional<FileSystemObject> getFileSystemObject(String path) {
        if (path.isEmpty()) { return Optional.empty(); }
        boolean directorySearch = false;
        if (path.endsWith("/")) { directorySearch = true; }  // Paths ending in / can only reference directories
        List<String> pathArgs = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
        if (pathArgs.isEmpty()) { return Optional.of(root); }
        Directory workingDirectory;
        if (pathArgs.getFirst().equals(".")) {  // starting in current directory
            workingDirectory = this.currentDirectory;
        } else {
            workingDirectory = this.root;
        }
        for (int i = 0; i < pathArgs.size(); i++) {
            if (i < pathArgs.size() - 1) {
                switch (pathArgs.get(i)) {
                    case ".":
                        continue;
                    case "..":
                        workingDirectory = workingDirectory.getParent();
                        break;
                    default:
                        Optional<FileSystemObject> optionalFSO = workingDirectory.findChild(pathArgs.get(i));
                        if (optionalFSO.isEmpty() || !(optionalFSO.get() instanceof Directory)) {
                            return Optional.empty();
                        } else {
                            workingDirectory = (Directory) optionalFSO.get();
                        }
                }
            } else {
                switch(pathArgs.get(i)) {
                    case ".":
                        return Optional.of(workingDirectory);
                    case "..":
                        return Optional.of(workingDirectory.getParent());
                    default:
                        Optional<FileSystemObject> foundObject = workingDirectory.findChild(pathArgs.get(i));
                        if (directorySearch && (foundObject.isEmpty() || !(foundObject.get() instanceof Directory))) { return Optional.empty(); }
                        return workingDirectory.findChild(pathArgs.get(i));
                }
            }
        }
        return Optional.empty();
    }

    public boolean moveFSO(String movedFSOPath, String newLocationPath) {
        Optional<FileSystemObject> optionalMovedFSO = getFileSystemObject(movedFSOPath);
        Optional<FileSystemObject> optionalNewDirectory = getFileSystemObject(newLocationPath);

        if (optionalMovedFSO.isEmpty() || optionalNewDirectory.isEmpty()) { return false; }
        FileSystemObject movedFSO = optionalMovedFSO.get();
        FileSystemObject newDirectory = optionalNewDirectory.get();
        if (!(newDirectory instanceof Directory newParent)) { return false; }
        updateParent(movedFSO, newParent);
        return true;
    }

    public boolean createFile(String name, Directory parent) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent);
        parent.addChild(newFile);
        return true;
    }

    public boolean createFile(String name, String parentPath) {
        Optional<FileSystemObject> parentOptional = getFileSystemObject(parentPath);
        if (parentOptional.isEmpty() || !(parentOptional.get() instanceof Directory parent) || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent);
        parent.addChild(newFile);
        return true;
    }

    public boolean createFile(String name, Directory parent, String contents) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent, contents);
        parent.addChild(newFile);
        return true;
    }

    public boolean createFile(String name, String parentPath, String contents) {
        Optional<FileSystemObject> parentOptional = getFileSystemObject(parentPath);
        if (parentOptional.isEmpty() || !(parentOptional.get() instanceof Directory parent) || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent, contents);
        parent.addChild(newFile);
        return true;
    }

    public boolean createDirectory(String name, Directory parent) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        Directory newDirectory = new Directory(name, parent);
        parent.addChild(newDirectory);
        return true;
    }

    public boolean createDirectory(String name, String parentPath) {
        Optional<FileSystemObject> parentOptional = getFileSystemObject(parentPath);
        if (parentOptional.isEmpty() || !(parentOptional.get() instanceof Directory parent) || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        Directory newDirectory = new Directory(name, parent);
        parent.addChild(newDirectory);
        return true;
    }

    public Directory getRoot() {
        return this.root;
    }

    private void updateParent(FileSystemObject FSO, Directory newParent) {
        FSO.getParent().removeChild(FSO);
        newParent.addChild(FSO);
        FSO.setParent(newParent);
        FSO.updatePath();
    }

    public boolean moveCurrentDirectory(String path) {
        Optional<FileSystemObject> newDirectoryOptional = getFileSystemObject(path);
        if (newDirectoryOptional.isEmpty() || !(newDirectoryOptional.get() instanceof Directory)) { return false; }
        this.currentDirectory = (Directory) newDirectoryOptional.get();
        return true;
    }

}
