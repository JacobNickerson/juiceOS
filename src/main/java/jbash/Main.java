package jbash;


import jbash.commands.Command;
import jbash.commands.CommandFactory;
import jbash.filesystem.FileSystemAPI;
import jbash.filesystem.FileSystemObject;
import jbash.filesystem.File;
import jbash.filesystem.Directory;
import jbash.parser.JBParserException;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static jbash.parser.JBashParser.parseCommand;


public class Main {
    private static final Scanner userIn = new Scanner(System.in);
    private static final boolean debug = false;
    private static FileSystemAPI FSAPI = FileSystemAPI.getInstance();
    private static final char shell_prompt = '$';

    public static void main(String[] args) {

        while (true) {
            // Prompt
            System.out.print("[jbash] " + (FSAPI.getCurrentDirectory().getPath()) + " " + shell_prompt + " ");

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
                tokens.forEach(System.out::print);
                System.out.println();
            }

            // Gather command name
            var cmdName = tokens.getFirst();

            // Execute command with remaining arguments
            Command cmd = CommandFactory.get(cmdName);
            int returnCode = cmd.execute(tokens.subList(1, tokens.size()));
        }
    }
}