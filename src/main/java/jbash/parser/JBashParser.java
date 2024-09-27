package jbash.parser;

import jbash.jbashutils.JBashUtils;

import java.util.*;
import java.util.stream.Collectors;

import static jbash.jbashutils.JBashUtils.findFirstOf;

public final class JBashParser {
    private final ArrayList<Token> tokens = new ArrayList<>();
    JBashParser(ArrayList<Token> tokens) {
        this.tokens.addAll(tokens);
    }

    /**
     * Processes redirection operators on the internal token list.
     */
    private void processRedirects() {
        if (tokens.isEmpty()) throw new RuntimeException("processRedirects() called on empty token list.");

//        for (int i = 0; i < tokens.size(); i++) {
//            var t = tokens.get(i);
//            if (t.type() = TokenType.Great) {
//                var next = tokens.get(++i);
//                while (next.type() != TokenType.Whitespace) {
//
//                }
//            }
//        }
    }

    public static ArrayList<String> parseCommand(String input) throws JBParserException {
        if (input.isEmpty()) { return new ArrayList<>(); }
        var parser = new JBashParser(JBashLexer.lexCommand(input));

        // Parsing phase
        parser.processRedirects();

        return new ArrayList<>(
                List.of(parser.tokens.parallelStream()
                        .map(Token::lexeme)
                        .reduce("", (s, t) -> s + t)
                        .split("\0")));
    }

}
