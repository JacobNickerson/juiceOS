package jbash.environment;

import jbash.filesystem.FileSystemAPI;

import java.util.Arrays;
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

    // File descriptor -> buffer.
    private final HashMap<Integer, FileBuffer> fds;

    /**
     * Flushes the buffer of a file descriptor out to its destination.
     * For STD_IN, this just clears it.
     * For STD_OUT and STD_ERR, this is to the screen.
     * For any other file descriptor, this writes to that file.
     * @param fd File descriptor to write to.
     */
    public void fdFlush(int fd) {
        switch (fd) {
            case STD_IN -> {
                consume(STD_IN);  // just clears STD_IN
            }
            case STD_OUT -> System.out.print(consume(STD_OUT).orElse(""));
            case STD_ERR -> System.out.print("\033[31m"+consume(STD_ERR).orElse("")+"\033[0m");
            default -> {
                var fbuf = fds.get(fd);
                if (fbuf == null) {
                    send(STD_ERR, "No such buffer associated with "+fd+"\n");
                    fdFlush(STD_ERR);
                    return;
                }
                if (!fbuf.mode.contains("w")) {
                    send(STD_ERR, "File descriptor "+fd+" not open for writing\n");
                    fdFlush(STD_ERR);
                    return;
                }

                fbuf.file.setContents(fbuf.file.getContents() + fbuf.buffer);
                fbuf.buffer = "";
            }
        }
    }

    private JBashEnvironment() {
        // Perform any initialization of the environment
        environment = new HashMap<>();
        environment.put("PS1", "$ ");
        environment.put("PWD", "/");
        environment.put("HOME", "/");
        environment.put("?", "0");

        fds = new HashMap<>();
        fds.put(STD_IN, new FileBuffer("stdin", "rw"));
        fds.put(STD_OUT, new FileBuffer("stdout", "rw"));
        fds.put(STD_ERR, new FileBuffer("stderr", "rw"));
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
        var fbuf = fds.get(fd);
        if (fbuf == null) return;

        fbuf.buffer += msg;
    }

    /**
     * Clears and returns the buffer in file descriptor <code>fd</code>.
     */
    public Optional<String> consume(int fd) {
        var fbuf = instance.fds.get(fd);
        if (fbuf == null) return Optional.empty();

        var msg = fbuf.buffer;
        fbuf.buffer = "";
        return Optional.of(msg);
    }
}
