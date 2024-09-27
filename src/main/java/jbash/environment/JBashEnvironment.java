package jbash.environment;

import java.util.HashMap;

/**
 * Currently a thin wrapper around a HashMap, but may provide more utilities later.
 */
public final class JBashEnvironment {
    private static JBashEnvironment instance = null;
    private final HashMap<String, String> environment;

    private JBashEnvironment() {
        // Perform any initialization of the environment
        environment = new HashMap<>();
        environment.put("PS1", "$ ");
        environment.put("PWD", "/");
        environment.put("HOME", "/");
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
        getInstance().environment.put(var, val);
    }

    /**
     * Returns a string value associated with <code>var</code>.
     * @param var Environment variable to get value of.
     * @return Value of <code>var</code>.
     */
    public String get(String var) {
        return getInstance().environment.get(var) == null
                ? ""
                : environment.get(var);
    }
}
