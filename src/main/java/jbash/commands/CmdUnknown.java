package jbash.commands;

import jbash.environment.JProcess;

import java.util.List;

class CmdUnknown extends Command {
    CmdUnknown(String name, JProcess parent) {
        super(name, parent);
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
