import Commands.CmdEcho;
import Commands.Command;
import Commands.CommandFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner userIn = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            // Prompt
            System.out.print("[jbash]> ");

            // VERY basic argument parsing (on spaces)
            var input = new ArrayList<>(Arrays.asList(
                    userIn.nextLine().split("\\s+"))
            );

            // Gather command name
            var cmdName = input.stream().findFirst();
            if (cmdName.isEmpty()) { continue; }
            input.removeFirst();

            // Execute command with remaining arguments
            Command cmd = CommandFactory.get(cmdName.get());
            cmd.execute(input);
        }
    }
}