package jbash.commands;

import jbash.filesystem.Directory;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CmdLs extends Command {
    CmdLs(String name) {
        super("ls");
    }

    @Override
    public String getHelp() {
        return "Usage: ls [directory]";
    }

    @Override
    public int execute(List<String> args) {
        FileSystemAPI FSAPI = FileSystemAPI.getInstance();
        boolean multiplePaths = false;
        if (args.isEmpty()) {
            List<FileSystemObject> currentFiles = FSAPI.getCurrentDirectory().getChildren();
            if (currentFiles.isEmpty()) { return 0; }
            for (FileSystemObject file : currentFiles) {
                System.out.print(file.getName());
                System.out.print(" ");
            }
            System.out.println();
        } else {
            if (args.size() > 1) { multiplePaths = true; }
            for (String path : args) {
                Optional<FileSystemObject> directory = FSAPI.getFileSystemObject(path);
                if (directory.isEmpty()) { System.out.println("ls: cannot access '" + path + "': No such file or directory"); }
                else if (!(directory.get() instanceof Directory)) { System.out.println(directory.get().getName()); }
                else {
                    List<FileSystemObject> children = ((Directory) directory.get()).getChildren();
                    if (multiplePaths) { System.out.println(path + ":"); }
                    for (FileSystemObject child : children) {
                        System.out.print(child.getName());
                        System.out.print(" ");
                    }
                    System.out.println();
                }
            }
        }
        return 0;
    }
}
