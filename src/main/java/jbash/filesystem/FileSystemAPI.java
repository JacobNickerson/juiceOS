package jbash.filesystem;

import jbash.environment.JBashEnvironment;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileSystemAPI {
    private final Directory root;
    private Directory currentDirectory;

    final Disk disk;
    private static FileSystemAPI instance = null;
    private static final JBashEnvironment ENV = JBashEnvironment.getInstance();

    private FileSystemAPI() {
        this.root = new Directory("root", null);
        this.disk = new Disk("fs10m.jfs", 10*1024*1024);  // 10MB filesystem
        this.currentDirectory = this.root;
    }

    public static FileSystemAPI getInstance() {
        if (instance == null) { instance = new FileSystemAPI(); }
        return instance;
    }

    public void reset() {  // needed for unit testing
        instance = new FileSystemAPI();
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

    /**
     * Returns a file system object optional with the filepath <code>path</code>
     * If no such object exists, Optional.empty() is returned.
     * @param path Absolute or relative filepath to retrieve the file.
     * @return The file system object, or Optional.empty().
     */
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

    /**
     * Moves a file system object from its current location to a new location. Returns true
     * if successful and false if unsuccessful. Fails if the object that is being moved cannot be found
     * or if the new location cannot be found or is found but is not a directory.
     * @param movedFSOPath Absolute or relative path to the object to be moved
     * @param newLocationPath Absolute or relative path to the directory to move to
     * @return true if operation is successful, else false
     */
    public boolean moveFSO(String movedFSOPath, String newLocationPath) {
        Optional<FileSystemObject> optionalMovedFSO = getFileSystemObject(movedFSOPath);
        Optional<Directory> optionalNewDirectory = getFileSystemDirectory(newLocationPath);

        if (optionalMovedFSO.isEmpty() || optionalNewDirectory.isEmpty()) { return false; }
        FileSystemObject movedFSO = optionalMovedFSO.get();
        Directory newParent = optionalNewDirectory.get();
        updateParent(movedFSO, newParent);
        return true;
    }

    /**
     * Attempts to create a file with the specified name belonging to the specified
     * parent directory. Returns a boolean, true if file creation is successful and false otherwise.
     * Will fail if no name is inputted, if the parent reference is null, or if the parent already
     * has a child with the same name as specified.
     * @param name The name of the new file being created
     * @param parent A reference to the directory that owns the new file
     * @return true if creation is successful, false otherwise
     */
    public boolean createFile(String name, Directory parent) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent);
        parent.addChild(newFile);
        return true;
    }

    /**
     * Attempts to create a file with the specified name belonging to the parent directory with the absolute
     * or relative path <code>parentPath</code>. Returns a boolean, true if file creation is successful and
     * false otherwise. Will fail if no name is inputted, if the parent directory is not found, or if the
     * parent directory already has a child with the same name as specified.
     * @param name The name of the new file to be created
     * @param parentPath The absolute or relative path to the parent directory
     * @return true if operation is successful, false otherwise
     */
    public boolean createFile(String name, String parentPath) {
        Optional<Directory> parentOptional = getFileSystemDirectory(parentPath);
        if (parentOptional.isEmpty() || name.isEmpty()) { return false; }
        Directory parent = parentOptional.get();
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent);
        parent.addChild(newFile);
        return true;
    }

    /**
     * Attempts to create a file with the specified name belonging to the specified
     * parent directory and with contents set to the inputted string. Returns a boolean,
     * true if file creation is successful and false otherwise. Will fail if no name is
     * inputted, if the parent reference is null, or if the parent already has a child
     * with the same name as specified.
     * @param name The name of the new file being created
     * @param parent A reference to the directory that owns the new file
     * @param contents The contents of the new file being creates
     * @return true if creation is successful, false otherwise
     */
    public boolean createFile(String name, Directory parent, String contents) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent, contents);
        parent.addChild(newFile);
        return true;
    }

     /**
     * Attempts to create a file with the specified name belonging to the parent directory with the absolute
     * or relative path <code>parentPath</code> and with contents set to the inputted string. Returns a boolean
     * true if file creation is successful and false otherwise. Will fail if no name is inputted, if the parent directory
     * is not found, or if the parent directory already has a child with the same name as specified.
     * @param name The name of the new file to be created
     * @param parentPath The absolute or relative path to the parent directory
     * @param contents The contents of the new file being created
     * @return true if operation is successful, false otherwise
     */
    public boolean createFile(String name, String parentPath, String contents) {
        Optional<Directory> parentOptional = getFileSystemDirectory(parentPath);
        if (parentOptional.isEmpty() || name.isEmpty()) { return false; }
        Directory parent = parentOptional.get();
        if (parent.findChild(name).isPresent()) { return false; }
        File newFile = new File(name, parent, contents);
        parent.addChild(newFile);
        return true;
    }

    /**
     * Attempts to create a directory with the specified name belonging to the specified
     * parent directory. Returns a boolean, true if directory creation is successful and false otherwise.
     * Will fail if no name is inputted, if the parent reference is null, or if the parent already
     * has a child with the same name as specified.
     * @param name The name of the new directory being created
     * @param parent A reference to the directory that owns the new file
     * @return true if creation is successful, false otherwise
     */
    public boolean createDirectory(String name, Directory parent) {
        if (parent == null || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        Directory newDirectory = new Directory(name, parent);
        parent.addChild(newDirectory);
        return true;
    }

    /**
     * Attempts to create a directory with the specified name belonging to the parent directory with the absolute
     * or relative path <code>parentPath</code>. Returns a boolean, true if file creation is successful and
     * false otherwise. Will fail if no name is inputted, if the parent directory is not found, or if the
     * parent directory already has a child with the same name as specified.
     * @param name The name of the new directory to be created
     * @param parentPath The absolute or relative path to the parent directory
     * @return true if operation is successful, false otherwise
     */
    public boolean createDirectory(String name, String parentPath) {
        Optional<FileSystemObject> parentOptional = getFileSystemObject(parentPath);
        if (parentOptional.isEmpty() || !(parentOptional.get() instanceof Directory parent) || name.isEmpty()) { return false; }
        if (parent.findChild(name).isPresent()) { return false; }
        Directory newDirectory = new Directory(name, parent);
        parent.addChild(newDirectory);
        return true;
    }

    /**
     * Attempts to create a directory with the specified name belonging to the parent directory with the absolute
     * or relative path <code>parentPath</code>. Returns a boolean, true if file creation is successful and
     * false otherwise. Will fail if no name is inputted, if the parent directory is not found, or if the
     * parent directory already has a child with the same name as specified.
     *
     * Attempts to create a directory with the absolute or relative filepath <code>path</code>.
     * Returns a boolean, true if directory creation is successful and false otherwise. Will fail if
     * the path leads to an already existing object or path goes through another directory that does
     * not already exist.
     * @param path The path to the new directory to be created
     * @return true if operation is successful, false otherwise
     */
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

    /**
     * Returns the root directory of the file system.
     * @return Root directory
     */
    public Directory getRoot() {
        return this.root;
    }

    /**
     * Changes the ownership of a filesystem object from its current parent to a new parent <code>newParent</code>.
     * @param FSO File system object being reassigned
     * @param newParent Reference to the new parent of the object
     */
    private void updateParent(FileSystemObject FSO, Directory newParent) {
        FSO.getParent().removeChild(FSO);
        newParent.addChild(FSO);
        FSO.setParent(newParent);
        FSO.updatePath();
    }

    /**
     * Changes the environment variable PWD to the absolute filepath of the directory with absolute or
     * relative filepath <code>path</code> and changes currentDirectory reference to the directory with absolute
     * or relative filepath <code>path</code>. Returns a bool indicating if the operation is successful.
     * @param path Absolute or relative path to new working directory
     * @return true if operation is successful, otherwise false
     */
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
     * @return true if operation was a success, false otherwise.
     */
    public boolean writeToFile(String path, String content) {
        var file = getFileSystemFile(path).orElse(null);
        if (file == null) return false;

        file.setContents(content);
        return true;
    }

    /**
     * Returns a reference to the current directory.
     * @return The current directory
     */
    public Directory getCurrentDirectory() {
        return this.currentDirectory;
    }

}
