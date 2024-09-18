package jbash.commands;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    private final String name;
    Command(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    public abstract String getHelp();
    public abstract int execute(List<String> args);
}