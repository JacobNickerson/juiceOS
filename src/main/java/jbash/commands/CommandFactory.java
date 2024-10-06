package jbash.commands;

import jbash.environment.JProcess;

public class CommandFactory {
    private final JProcess parent;
    public CommandFactory(JProcess parent) {
        this.parent = parent;
    }
    public Command get(String cmdName) {
        return switch (cmdName) {
            case "echo"     -> new CmdEcho(cmdName, parent);
            case "pwd"      -> new CmdPwd(cmdName, parent);
            case "cd"       -> new CmdCd(cmdName, parent);
            case "mkdir"    -> new CmdMkdir(cmdName, parent);
            case "ls"       -> new CmdLs(cmdName, parent);
            case "jbash"    -> new CmdJBash(cmdName, parent);
            default         -> new CmdUnknown(cmdName, parent);
        };
    }
}
