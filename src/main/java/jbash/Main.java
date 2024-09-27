package jbash;


import jbash.environment.JBashEnvironment;
import jbash.filesystem.FileSystemAPI;
import jbash.parser.JBParserException;
import jbash.commands.JBashProcess;

import java.util.ArrayList;
import java.util.Scanner;

import static jbash.parser.JBashParser.parseCommand;

public class Main {
    private static final Scanner userIn = new Scanner(System.in);
    private static final boolean debug = false;
    private static final FileSystemAPI FSAPI = FileSystemAPI.getInstance();
    private static final JBashEnvironment ENV = JBashEnvironment.getInstance();
    private static final char shell_prompt = '$';

    public static void main(String[] args) {
        while (true) {
            // Prompt
            System.out.print("[jbash] " + ENV.get("PWD") + " " + ENV.get("PS1"));

            ArrayList<String> tokens;
            try { tokens = parseCommand(userIn.nextLine()); }
            catch (JBParserException e) {
                // Parsing failed: let the user know what went wrong
                System.out.println(e.getMessage());
                continue;
            }
            if (tokens.isEmpty()) continue;

            // Delete later: this just prints our tokens out
            if (debug) {
                tokens.forEach(t -> System.out.print("["+t+"]"));
                System.out.println();
            }

            // Gather command name
            var cmdName = tokens.getFirst();
            int returnCode = new JBashProcess().exec(cmdName, tokens.subList(1, tokens.size()));

            System.out.print(ENV.consume(ENV.STD_OUT).orElse(""));
            System.out.print("\033[31m"+ENV.consume(ENV.STD_ERR).orElse("")+"\033[0m");
        }
    }
}