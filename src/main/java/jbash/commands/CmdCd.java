package jbash.commands;

import jbash.filesystem.FileSystemAPI;

import java.util.List;

public class CmdCd extends Command {
    CmdCd(String name) {
        super("cd");
    }

    @Override
    public String getHelp() {
        return "Usage: cd [directory]";
    }

    @Override
    public int execute(List<String> args) {
        if (args.size() > 1) { System.out.println("cd: too many arguments"); return 1; }
        if (args.isEmpty()) {
            FileSystemAPI.getInstance().moveCurrentDirectory("");
            return 0;
        }
        if (FileSystemAPI.getInstance().moveCurrentDirectory(args.getFirst())) { return 0; }
        // TODO: Add explicit error messaging
        System.out.println("cd: cringe bro");
        return 1;
    }
}
