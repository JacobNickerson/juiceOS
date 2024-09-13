package Commands;

import java.util.ArrayList;

public class CmdUnknown extends Command {
    CmdUnknown(String name) {
        super(name);
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public int execute(ArrayList<String> args) {
        System.out.println("Unknown command: " + getName());
        return -1;
    }
}
