import Commands.CmdEcho;
import Commands.Command;
import Commands.CommandFactory;
import JBashUtils.Result;
import Parser.JBParserException;
import Parser.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static Parser.JBashParser.parseCommand;

public class Main {
    private static final Scanner userIn = new Scanner(System.in);
    private static final boolean debug = false;

    public static void main(String[] args) {
        while (true) {
            // Prompt
            System.out.print("[jbash]> ");

            ArrayList<Token> tokens;
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

            var words = tokens.stream()
                              .map(Token::lexeme)
                              .collect(Collectors.toList());

            // Gather command name
            var cmdName = words.isEmpty() ? "" : words.getFirst();

            // Execute command with remaining arguments
            Command cmd = CommandFactory.get(cmdName);
            int returnCode = cmd.execute(words.subList(1, words.size()));
        }
    }
}