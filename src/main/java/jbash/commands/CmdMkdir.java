package jbash.commands;

import jbash.filesystem.File;
import jbash.filesystem.FileSystemAPI;

import java.util.List;

public class CmdMkdir extends Command {
    CmdMkdir(String name) {
        super("cd");
    }

    @Override
    public String getHelp() {
        return "Usage: mkdir [directory]";
    }

    @Override
    public int execute(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("mkdir: missing operand");
            System.out.println("Try 'mkdir --help' for more information." );
        }
        FileSystemAPI FSAPI = FileSystemAPI.getInstance();
        for (String arg : args) {
            if (!FSAPI.createDirectory(arg)) {
                System.out.println("mkdir: cannot create directory '" + arg + "'");
            }
        }
        return 0;
    }
}
