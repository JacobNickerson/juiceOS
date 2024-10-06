package jbash.commands;

import jbash.environment.JProcess;

import java.util.List;

public abstract class Command {
    final String name;
    final JProcess parent;
    static int STD_IN;
    static int STD_OUT;
    static int STD_ERR;

    Command(String name, JProcess parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return this.name;
    }

    /** Read from STD_IN. */
    String cmdRead() {
        return parent.consume(STD_IN).orElse("");
    }

    /** Prints object o to STD_OUT. */
    void cmdPrint(Object o) {
        parent.send(STD_OUT, o.toString());
    }

    /** Prints object o to STD_OUT. */
    void cmdPrintln(Object o) {
        parent.send(STD_OUT, o.toString()+"\n");
    }

    /** Prints a blank line to STD_OUT. */
    void cmdPrintln() {
        parent.send(STD_OUT, "\n");
    }

    /** Prints object o to STD_ERR. */
    void cmdErr(Object o) {
        parent.send(STD_ERR, o.toString());
    }

    /** Prints object o to STD_ERR. */
    void cmdErrln(Object o) {
        parent.send(STD_ERR, o.toString()+"\n");
    }

    /** Prints a blank line to STD_ERR. */
    void cmdErrln() {
        parent.send(STD_ERR, "\n");
    }

    public Command setChannels(int IN, int OUT, int ERR) {
        STD_IN = IN;
        STD_OUT = OUT;
        STD_ERR = ERR;

        return this;
    }

    public abstract String getHelp();
    public abstract int execute(List<String> argv);

    public void err(String errorMessage) {
        cmdErrln(name + ": " + errorMessage);
    }
}