package jbash.commands;

public class CommandFactory {
    public static Command get(String cmdName) {
        return switch (cmdName) {
            case "echo" -> new CmdEcho(cmdName);
            case "pwd" -> new CmdPwd(cmdName);
            case "cd" -> new CmdCd(cmdName);
            case "mkdir" -> new CmdMkdir(cmdName);
            default -> new CmdUnknown(cmdName);
        };
    }
}
