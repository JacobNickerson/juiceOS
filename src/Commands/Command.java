package Commands;

import java.util.ArrayList;

public abstract class Command {
    private final String name;
    Command(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public abstract String getHelp();
    public abstract int execute(ArrayList<String> args);
}