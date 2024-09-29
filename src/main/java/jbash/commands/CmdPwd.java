package jbash.commands;

import jbash.environment.JProcess;
import jbash.filesystem.Directory;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;

import java.util.List;
import java.util.Optional;

class CmdPwd extends Command {
    CmdPwd(String name, JProcess parent) {
        super(name, parent);
    }

    @Override
    public String getHelp() {
        return "Usage: pwd";
    }

    @Override
    public int execute(List<String> argv) {
        Optional<FileSystemObject> currentDirectoryOptional = FileSystemAPI.getInstance().getFileSystemObject("./");
        if (currentDirectoryOptional.isEmpty() || !(currentDirectoryOptional.get() instanceof Directory currentDirectory)) { cmdErrln("ERROR: I HAVE NO IDEA WHAT HAPPENED"); return 1; }
        cmdPrintln(currentDirectory.getPath());
        return 0;
    }
}
