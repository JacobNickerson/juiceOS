package jbash.commands;

import jbash.environment.JBashEnvironment;

import java.util.List;

/**
 * Light abstraction over command-execution.
 */
public class JBashProcess {
    // File descriptors
    private final int STD_IN;
    private final int STD_OUT;
    private final int STD_ERR;

    public JBashProcess() {
        STD_IN = 0;
        STD_OUT = 1;
        STD_ERR = 2;
    }

    public JBashProcess(int fdIN, int fdOUT, int fdERR) {
        STD_IN = fdIN;
        STD_OUT = fdOUT;
        STD_ERR = fdERR;
    }

    public int exec(String cmdName, List<String> argv) {
        int code = CommandFactory.get(cmdName)
                                 .setFds(STD_IN, STD_OUT, STD_ERR)
                                 .execute(argv);
        JBashEnvironment.getInstance().fdFlush(STD_IN);
        JBashEnvironment.getInstance().fdFlush(STD_OUT);
        JBashEnvironment.getInstance().fdFlush(STD_ERR);

        return code;
    }
}
