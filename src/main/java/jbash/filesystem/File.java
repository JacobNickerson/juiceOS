package jbash.filesystem;

public class File extends FileSystemObject {
    private Directory parent;
    private String contents;

    public File(String name) {
        super(name);
    }
}
