package jbash.commands;

import jbash.environment.JBashEnvironment;

import java.util.List;

abstract class Command {
    private final String name;
    private static final JBashEnvironment ENV = JBashEnvironment.getInstance();
    private static int STD_IN;
    private static int STD_OUT;
    private static int STD_ERR;

    Command(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }

    /** Read from STD_IN. */
    String cmdRead() {
        return ENV.consume(STD_IN).orElse("");
    }

    /** Prints object o to STD_OUT. */
    void cmdPrint(Object o) {
        ENV.send(STD_OUT, o.toString());
    }

    /** Prints object o to STD_OUT. */
    void cmdPrintln(Object o) {
        ENV.send(STD_OUT, o.toString()+"\n");
    }

    /** Prints a blank line to STD_OUT. */
    void cmdPrintln() {
        ENV.send(STD_OUT, "\n");
    }

    /** Prints object o to STD_ERR. */
    void cmdErr(Object o) {
        ENV.send(STD_ERR, o.toString());
    }

    /** Prints object o to STD_ERR. */
    void cmdErrln(Object o) {
        ENV.send(STD_ERR, o.toString()+"\n");
    }

    /** Prints a blank line to STD_ERR. */
    void cmdErrln() {
        ENV.send(STD_ERR, "\n");
    }

     public Command setFds(int IN, int OUT, int ERR) {
        STD_IN = IN;
        STD_OUT = OUT;
        STD_ERR = ERR;

        return this;
    }

    public abstract String getHelp();
    public abstract int execute(List<String> argv);
}