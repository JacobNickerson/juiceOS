package jbash.filesystem;

public abstract class FileSystemObject {
    private String name;
    private Directory parent;
    private String path;

    public FileSystemObject(String name, Directory parent) {
        this.name = name;
        if (parent == null) {
            this.parent = (Directory) this;
            this.path = "/";
        } else {
            this.parent = parent;
            updatePath();
        }
    }

    public String getPath() {
        return this.path;
    }

    public Directory getParent() {
        return this.parent;
    }

    public void updatePath() {
        // check if parent is root
        if (this.getParent().getParent() == this.getParent()) { this.path = "/" + name; }
        else { this.path = parent.getPath() + "/" + name; }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setParent(Directory newParent) {
       this.parent = newParent;
    }
}
