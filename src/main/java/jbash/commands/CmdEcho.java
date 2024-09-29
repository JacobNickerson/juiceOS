package jbash.commands;

import jbash.environment.JProcess;

import java.util.List;

class CmdEcho extends Command {
    CmdEcho(String name, JProcess parent) {
        super(name, parent);
    }

    @Override
    public String getHelp() {
        return "Usage: echo [STRING]";
    }

    @Override
    public int execute(List<String> argv) {
        for (String arg : argv) {
            cmdPrint(arg);
            cmdPrint(" ");
        }
        cmdPrintln();
        return 0;
    }
}
