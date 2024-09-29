package jbash.environment;

import java.util.*;

/**
 * Singleton representing many kernel utilities.
 */
public final class JEnvironment implements Cloneable {
    private final HashMap<String, String> environment;

    public JEnvironment() {
        // Perform any initialization of the environment
        environment = new HashMap<>();
        environment.put("PS1", "$ ");
        environment.put("PWD", "/");
        environment.put("HOME", "/");
        environment.put("?", "0");
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

    @Override
    public Object clone() {
        // This is stupid. this object should always be cloneable...
        try {
            return super.clone();
        } catch (Exception ignored){
            return null;
        }
    }
}
