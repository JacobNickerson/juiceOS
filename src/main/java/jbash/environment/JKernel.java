package jbash.environment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class JKernel {
    private static JKernel kernel = null;

    private final HashSet<FileBuffer> openFiles;
    public JProcess current;  // TODO: THIS SHOULD BE PRIVATE.

    public final FileBuffer STD_IN;
    public final FileBuffer STD_OUT;
    public final FileBuffer STD_ERR;

    public JKernel() {
        STD_IN = new FileBuffer("STDIN",  "wr");
        STD_OUT = new FileBuffer("STDOUT",  "wr");
        STD_ERR = new FileBuffer("STDERR",  "wr");

        openFiles = new HashSet<>(List.of(STD_IN, STD_OUT, STD_ERR));

        // INIT: Initial process
        current = new JProcess(new JEnvironment());
        current.fds.put(0, STD_IN);
        current.fds.put(1, STD_OUT);
        current.fds.put(2, STD_ERR);
    }

    public static synchronized JKernel getInstance() {
        if (kernel == null) { kernel = new JKernel(); }
        return kernel;
    }

    /**
     * Runs a new command as a new process. The existing environment is cloned over,
     * but doesn't change the parent's environment.
     * <p>
     * NOTE: THIS IS DIFFERENT FROM THE STANDARD UNIX FORK.
     *       Unix's fork creates a new process, but doesn't immediately run a command with it.
     *       The reason for the difference here, is that fork()ing in Unix copies over the
     *       _entire_ environment for a process -- not just its variables, but its call stack, etc.
     *       Even things like the heap, code and other library regions are shared.
     *       We _definitely_ don't have the utilities to be doing all of that just yet.
     * <p>
     * FURTHER NOTE: THIS DOES NOT RUN IN PARALLEL WITH THE PARENT PROCESS.
     *
     * @param cmdName Command to run as new process.
     * @param argv Argument vector to supply to command.
     * @return Exit status of child process.
     */
    public int fork(String cmdName, List<String> argv) {
        // Make a new process that inherits the old environment and runs a specific command
        try {
            JProcess parent = current;
            current = new JProcess((JEnvironment) current.env.clone());
            current.fds = parent.fds;
            current.run(cmdName, argv);
            current = parent;
            return 0;
        } catch (Exception e) { return 1; }
    }

    /**
     * Runs a new command as the current process.
     *
     * @param cmdName Command to run.
     * @param argv Argument vector to supply to command.
     * @return Exit status of process.
     */
    public int exec(String cmdName, List<String> argv) {
        return current.run(cmdName, argv);
    }

    /**
     * Open a file and return its file descriptor.
     * @param filepath Filepath of the file to open.
     * @param mode Mode
     * @return File descriptor associated with the open file, or -1 if error.
     */
    public int open(String filepath, String mode) {
        return -1;
    }

    /**
     * Closes a file associated with a file descriptor.
     * @param fd File descriptor associated with the file to close.
     * @return 0 on success of the operation, or -1 if error.
     */
    public int close(int fd) {
        return -1;
    }

    /**
     * Reads <code>count</code> bytes from the file associated with <code>fd</code> into <code>buffer.</code>
     * @param fd File descriptor.
     * @param buffer Out parameter -- byte buffer to read in.
     * @param count Number of bytes to read.
     * @return Number of bytes read. May be less than <code>count</code> in some cases (EOF being reached is one.) May also be -1 in case of error.
     */
    public int read(int fd, byte[] buffer, int count) {
        return -1;
    }

    /**
     * Writes <code>count</code> characters from <code>buffer</code> to the file associated with <code>fd</code>.
     * @param fd File descriptor.
     * @param buffer Buffer to write to disk.
     * @param count Number of characters to write from buffer.
     * @return Number of bytes written, or negative on error.
     */
    public int write(int fd, byte[] buffer, int count) {
        return -1;
    }


}
