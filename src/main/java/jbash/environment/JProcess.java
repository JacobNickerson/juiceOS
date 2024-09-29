package jbash.environment;

import jbash.commands.CommandFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Light abstraction over command-execution.
 */
public class JProcess {
    // File descriptor -> buffer.
    public HashMap<Integer, FileBuffer> fds = new HashMap<>(); // TODO: MAKE FINAL
    private final int MAX_BUF_SIZE = 80;

    // Environment
    public final JEnvironment env;

    // Constructor
    public JProcess(JEnvironment env) {
        this.env = env;
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
        var fbuf = fds.get(fd);
        if (fbuf == null) return Optional.empty();

        var msg = fbuf.buffer;
        fbuf.buffer = "";
        return Optional.of(msg);
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
            case 0 -> {
                consume(0);  // just clears STD_IN
            }
            case 1 -> System.out.print(consume(1).orElse(""));
            case 2 -> System.out.print("\033[31m"+consume(2).orElse("")+"\033[0m");
            default -> {
                var fbuf = fds.get(fd);
                if (fbuf == null) {
                    send(2, "No such buffer associated with "+fd+"\n");
                    fdFlush(2);
                    return;
                }
                if (!fbuf.mode.contains("w")) {
                    send(2, "File descriptor "+fd+" not open for writing\n");
                    fdFlush(2);
                    return;
                }

                fbuf.file.setContents(fbuf.file.getContents() + consume(fd));
            }
        }
    }

    public int run(String cmdName, List<String> argv) {
        int code = new CommandFactory(this).get(cmdName)
                                           .setChannels(0, 1, 2)
                                           .execute(argv);
        fdFlush(0);
        fdFlush(1);
        fdFlush(2);

        return code;
    }
}
