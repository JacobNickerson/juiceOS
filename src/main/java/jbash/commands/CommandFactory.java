package jbash.commands;

public class CommandFactory {
    public static Command get(String cmdName) {
        return switch (cmdName) {
            case "echo" -> new CmdEcho(cmdName);
            default -> new CmdUnknown(cmdName);
        };
    }
}
