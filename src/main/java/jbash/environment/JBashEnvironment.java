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
    private final int MAX_BUF_SIZE = 80;

    // File descriptor -> buffer.
    private final HashMap<Integer, FileBuffer> fds;

    /**
     * Open a file and return its file descriptor.
     * @param filepath Filepath of the file to open.
     * @param mode Mode
     * @return File descriptor associated with the open file, or -1 if error.
     */
    int open(String filepath, String mode) {
        return -1;
    }

    /**
     * Closes a file associated with a file descriptor.
     * @param fd File descriptor associated with the file to close.
     * @return 0 on success of the operation, or -1 if error.
     */
    int close(int fd) {
        return -1;
    }

    /**
     * Reads <code>count</code> bytes from the file associated with <code>fd</code> into <code>buffer.</code>
     * @param fd File descriptor.
     * @param buffer Out parameter -- byte buffer to read in.
     * @param count Number of bytes to read.
     * @return Number of bytes read. May be less than <code>count</code> in some cases (EOF being reached is one.) May also be -1 in case of error.
     */
    int read(int fd, byte[] buffer, int count) {
        return -1;
    }

    /**
     * Writes <code>count</code> characters from <code>buffer</code> to the file associated with <code>fd</code>.
     * @param fd File descriptor.
     * @param buffer Buffer to write to disk.
     * @param count Number of characters to write from buffer.
     * @return Number of bytes written, or negative on error.
     */
    int write(int fd, byte[] buffer, int count) {
        return -1;
    }

    /**
     * Flushes the buffer of a file descriptor out to its destination, and clears the buffer associated with it.
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

                fbuf.file.setContents(fbuf.file.getContents() + consume(fd));
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
     * Appends a message to file descriptor <code>fd</code>'s buffer.
     */
    public void send(int fd, String msg) {
        var fbuf = fds.get(fd);
        if (fbuf == null) return;

        fbuf.buffer += msg;

        // Automatically flush if buffer is getting too large
        if (fbuf.buffer.length() > MAX_BUF_SIZE) {
            fdFlush(fd);
        }
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
