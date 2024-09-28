package jbash.commands;

import java.util.List;

class CmdUnknown extends Command {
    CmdUnknown(String name) {
        super(name);
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public int execute(List<String> argv) {
        cmdErrln("Unknown command: " + getName());
        return -1;
    }
}
