package jbash.filesystem;

import jbash.environment.JBashEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileSystemAPI {
    private final Directory root;
    private Directory currentDirectory;

    final Disk disk;
    private static FileSystemAPI FSAPI = new FileSystemAPI();
    private static final JBashEnvironment ENV = JBashEnvironment.getInstance();

    private FileSystemAPI() {
        this.root = new Directory("root", null);
        this.disk = new Disk("fs10m.jfs", 10*1024*1024);  // 10MB filesystem
        this.currentDirectory = this.root;
    }

    public static FileSystemAPI getInstance() {
        return FSAPI;
    }

    public void reset() {
        this.FSAPI = new FileSystemAPI();
    }

    /**
     * Returns the directory with the filepath <code>path</code>.
     * If such an object exists but is not a directory, or no such object exists, Optional.empty() is returned.
     * @param path Absolute or relative filepath to retrieve the directory.
     * @return The directory, or Optional.empty().
     */
    public Optional<Directory> getFileSystemDirectory(String path) {
        var item = getFileSystemObject(path);
        if (item.isEmpty()) return Optional.empty();
        if (item.get() instanceof Directory dir) return Optional.of(dir);
        else return Optional.empty();
    }

    /**
     * Returns the file with the filepath <code>path</code>.
     * If such an object exists but is not a file, or no such object exists, Optional.empty() is returned.
     * @param path Absolute or relative filepath to retrieve the file.
     * @return The file, or Optional.empty().
     */
    public Optional<File> getFileSystemFile(String path) {
        var item = getFileSystemObject(path);
        if (item.isEmpty()) return Optional.empty();
        if (item.get() instanceof File file) return Optional.of(file);
        else return Optional.empty();
    }

    public Optional<FileSystemObject> getFileSystemObject(String path) {
        if (path.isEmpty()) { return Optional.empty(); }
        boolean directorySearch = path.endsWith("/"); // Paths ending in / can only reference directories
        List<String> pathArgs = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
        if (pathArgs.isEmpty()) { return Optional.of(root); }
        Directory workingDirectory;
        if (pathArgs.getFirst().equals("/")) {  // starting in root directory
            workingDirectory = this.root;
        } else {
            workingDirectory = this.currentDirectory;
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

    public boolean createDirectory(String path) {
        // split name from the rest of the path
        String strippedPath = path.replaceAll("[" + "/" + "]+$", "");
        if (strippedPath.isEmpty()) { return false; }
        int lastSlashIndex = strippedPath.lastIndexOf("/");
        String parentPath;
        if (lastSlashIndex == 0) { parentPath = "/"; }
        else if (lastSlashIndex == -1) { parentPath = "./"; }
        else { parentPath = strippedPath.substring(0, lastSlashIndex); }
        String name = strippedPath.substring(lastSlashIndex+1);
        return createDirectory(name, parentPath);
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
        Directory newDirectory = (path.isEmpty()
                ? getFileSystemDirectory(ENV.get("HOME"))
                : getFileSystemDirectory(path)
        ).orElse(null);
        if (newDirectory == null) return false;

        this.currentDirectory = newDirectory;
        ENV.set("PWD", currentDirectory.getPath());

        return true;
    }

    /**
     * Writes to the given file. Returns false if it could not write.
     * **Overwrites existing content.**
     * @param path Absolute or relative path to file.
     * @param content Content to write.
     * @return True if operation was a success, false otherwise.
     */
    public boolean writeToFile(String path, String content) {
        var file = getFileSystemFile(path).orElse(null);
        if (file == null) return false;

        file.setContents(content);
        return true;
    }

    public Directory getCurrentDirectory() {
        return this.currentDirectory;
    }

}
