package jbash.commands;

import jbash.filesystem.FileSystemAPI;

import java.util.List;

class CmdCd extends Command {
    CmdCd(String name) {
        super("cd");
    }

    @Override
    public String getHelp() {
        return "Usage: cd [directory]";
    }

    @Override
    public int execute(List<String> argv) {
        if (argv.size() > 1) { cmdErrln("cd: too many arguments"); return 1; }
        if (argv.isEmpty()) {
            FileSystemAPI.getInstance().moveCurrentDirectory("");
            return 0;
        }
        if (FileSystemAPI.getInstance().moveCurrentDirectory(argv.getFirst())) { return 0; }
        // TODO: Add explicit error messaging
        cmdErrln("cd: cringe bro");
        return 1;
    }
}
