package jbash.environment;

import java.util.HashMap;
import java.util.Optional;

/**
 * Currently a thin wrapper around a HashMap, but may provide more utilities later.
 */
public final class JBashEnvironment {
    private static JBashEnvironment instance = null;
    private final HashMap<String, String> environment;

    public final int STD_IN = 0;
    public final int STD_OUT = 1;
    public final int STD_ERR = 2;

    // File descriptor -> File content. Refers only to open files or STDIN,STDOUT,STDERR.
    private final HashMap<Integer, String> fds;

    private JBashEnvironment() {
        // Perform any initialization of the environment
        environment = new HashMap<>();
        environment.put("PS1", "$ ");
        environment.put("PWD", "/");
        environment.put("HOME", "/");
        environment.put("?", "0");

        fds = new HashMap<>();
    }

    public static synchronized JBashEnvironment getInstance() {
        if (instance == null) { instance = new JBashEnvironment(); }
        return instance;
    }

    /**
     * Sets environment variable.
     * @param var Name of environment variable to set.
     * @param val Name of value to assign <code>var</code>.
     */
    public void set(String var, String val) {
        environment.put(var, val);
    }

    /**
     * Returns a string value associated with <code>var</code>.
     * @param var Environment variable to get value of.
     * @return Value of <code>var</code>.
     */
    public String get(String var) {
        return environment.get(var) == null
                ? ""
                : environment.get(var);
    }

    /**
     * Sends a message to file descriptor <code>fd</code>.
     */
    public void send(int fd, String msg) {
        fds.put(fd, fds.getOrDefault(fd, "") + msg);
    }

    /**
     * Consumes any messages in file descriptor <code>fd</code>.
     */
    public Optional<String> consume(int fd) {
        var msg = instance.fds.get(fd);
        fds.put(fd, "");

        return msg == null
            ? Optional.empty()
            : Optional.of(msg);
    }
}
