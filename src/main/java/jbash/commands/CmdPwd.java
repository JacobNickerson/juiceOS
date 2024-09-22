package jbash.commands;

import jbash.filesystem.Directory;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;

import java.util.List;
import java.util.Optional;

public class CmdPwd extends Command {
    CmdPwd(String name) {
        super("pwd");
    }

    @Override
    public String getHelp() {
        return "Usage: pwd";
    }

    @Override
    public int execute(List<String> args) {
        Optional<FileSystemObject> currentDirectoryOptional = FileSystemAPI.getInstance().getFileSystemObject("./");
        if (currentDirectoryOptional.isEmpty() || !(currentDirectoryOptional.get() instanceof Directory currentDirectory)) { System.out.println("ERROR: I HAVE NO IDEA WHAT HAPPENED"); return 1; }
        System.out.println(currentDirectory.getPath());
        return 0;
    }
}
