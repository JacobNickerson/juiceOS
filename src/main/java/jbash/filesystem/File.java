package jbash.filesystem;

public class File extends FileSystemObject {
    private String contents;

    public File(String name, Directory parent) {
        super(name, parent);
    }

    public File(String name, Directory parent, String contents) {
        super(name, parent);
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String newContents) {
        this.contents = newContents;
    }
}
