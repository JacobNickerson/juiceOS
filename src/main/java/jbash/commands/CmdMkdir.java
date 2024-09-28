package jbash.commands;

import jbash.filesystem.FileSystemAPI;

import java.util.List;

class CmdMkdir extends Command {
    CmdMkdir(String name) {
        super("cd");
    }

    @Override
    public String getHelp() {
        return "Usage: mkdir [directory]";
    }

    @Override
    public int execute(List<String> argv) {
        if (argv.isEmpty()) {
            err("missing operand");
            cmdErrln("Try 'mkdir --help' for more information." );
        }
        FileSystemAPI FSAPI = FileSystemAPI.getInstance();
        for (String arg : argv) {
            if (!FSAPI.createDirectory(arg)) {
                err("cannot create directory '" + arg +"'");
            }
        }
        return 0;
    }
}
