package jbash.commands;

import jbash.filesystem.Directory;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;

import java.util.List;
import java.util.Optional;

class CmdLs extends Command {
    CmdLs(String name) {
        super("ls");
    }

    @Override
    public String getHelp() {
        return "Usage: ls [directory]";
    }

    @Override
    public int execute(List<String> argv) {
        FileSystemAPI FSAPI = FileSystemAPI.getInstance();
        boolean multiplePaths = false;
        if (argv.isEmpty()) {
            List<FileSystemObject> currentFiles = FSAPI.getCurrentDirectory().getChildren();
            if (currentFiles.isEmpty()) { return 0; }
            for (FileSystemObject file : currentFiles) {
                cmdPrint(file.getName());
                cmdPrint(" ");
            }
            cmdPrintln();
        } else {
            if (argv.size() > 1) { multiplePaths = true; }
            for (String path : argv) {
                Optional<FileSystemObject> directory = FSAPI.getFileSystemObject(path);
                if (directory.isEmpty()) { err("cannot access '" + path + "': No such file or directory"); }
                else if (!(directory.get() instanceof Directory)) { cmdPrintln(directory.get().getName()); }
                else {
                    List<FileSystemObject> children = ((Directory) directory.get()).getChildren();
                    if (multiplePaths) { cmdPrintln(path + ":"); }
                    for (FileSystemObject child : children) {
                        cmdPrint(child.getName());
                        cmdPrint(" ");
                    }
                    cmdPrintln();
                    if (multiplePaths) { cmdPrintln(); }
                }
            }
        }
        return 0;
    }
}
