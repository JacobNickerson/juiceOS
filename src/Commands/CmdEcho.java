package Commands;

import java.util.ArrayList;

public class CmdEcho extends Command {
    CmdEcho(String name) {
        super("echo");
    }

    @Override
    public String getHelp() {
        return "Usage: echo [STRING]";
    }

    @Override
    public int execute(ArrayList<String> args) {
        for (String arg : args) {
            System.out.print(arg);
            System.out.print(" ");
        }
        System.out.println();
        return 0;
    }
}
