package jbash.parser;

import java.util.*;

public final class JBashParser {
    private final ArrayList<Token> tokens = new ArrayList<>();
    JBashParser(ArrayList<Token> tokens) {
        this.tokens.addAll(tokens);
    }


    public static ArrayList<String> parseCommand(String input) throws JBParserException {
        if (input.isEmpty()) { return new ArrayList<>(); }
        var parser = new JBashParser(JBashLexer.lexCommand(input));

        return new ArrayList<>(
                List.of(parser.tokens.parallelStream()
                        .map(Token::lexeme)
                        .reduce("", (s, t) -> s + t)
                        .split("\0")));
    }
}
