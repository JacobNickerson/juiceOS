package jbash;


import jbash.commands.Command;
import jbash.commands.CommandFactory;
import jbash.parser.JBParserException;

import java.util.ArrayList;
import java.util.Scanner;

import static jbash.parser.JBashParser.parseCommand;


public class Main {
    private static final Scanner userIn = new Scanner(System.in);
    private static final boolean debug = true;

    public static void main(String[] args) {
        while (true) {
            // Prompt
            System.out.print("[jbash]> ");

            ArrayList<String> tokens;
            try { tokens = parseCommand(userIn.nextLine()); }
            catch (JBParserException e) {
                // Parsing failed: let the user know what went wrong
                System.out.println(e.getMessage());
                continue;
            }

            // Delete later: this just prints our tokens out
            if (debug) {
                tokens.forEach(System.out::print);
                System.out.println();
            }

            // Gather command name
            var cmdName = tokens.isEmpty() ? "" : tokens.getFirst();

            // Execute command with remaining arguments
            Command cmd = CommandFactory.get(cmdName);
            int returnCode = cmd.execute(tokens.subList(1, tokens.size()));
        }
    }
}