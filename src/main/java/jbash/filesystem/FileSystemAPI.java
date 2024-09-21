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

    public Optional<FileSystemObject> getFileSystemObject(String path) {
        List<String> pathArgs = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
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
                return workingDirectory.findChild(pathArgs.get(i));
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
        File newFile = new File(name, parent);
        parent.addChild(newFile);
        return true;
    }

    public boolean createFile(String name, Directory parent, String contents) {
        if (parent == null || name.isEmpty()) { return false; }
        File newFile = new File(name, parent, contents);
        parent.addChild(newFile);
        return true;
    }

    public boolean createDirectory(String name, Directory parent) {
        if (parent == null || name.isEmpty()) { return false; }
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

}
