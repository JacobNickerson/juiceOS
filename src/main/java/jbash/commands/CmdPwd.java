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
        Optional<Directory> currentDirectoryOptional = FileSystemAPI.getInstance().getFileSystemDirectory("./");
        if (currentDirectoryOptional.isEmpty()) { err("I don't think this is reachable?"); return 1; }
        cmdPrintln(currentDirectoryOptional.get().getPath());
        return 0;
    }
}
