package jbash.commands;

import java.util.List;

public class CmdUnknown extends Command {
    CmdUnknown(String name) {
        super(name);
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public int execute(List<String> args) {
        System.out.println("Unknown command: " + getName());
        return -1;
    }
}
