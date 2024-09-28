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

    public static void main(String[] args) {
        while (true) {
            // Prompt
            System.out.print("[jbash] " + ENV.get("PWD") + " " + (ENV.get("?").equals("0") ? ENV.get("PS1") : "× "));

            ArrayList<String> tokens;
            try { tokens = parseCommand(userIn.nextLine()); }
            catch (JBParserException e) {
                // Parsing failed: let the user know what went wrong
                ENV.send(ENV.STD_ERR, e.getMessage()+"\n");
                ENV.fdFlush(ENV.STD_OUT);
                ENV.fdFlush(ENV.STD_ERR);
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
            ENV.set("?", Integer.toString(returnCode));
        }
    }
}