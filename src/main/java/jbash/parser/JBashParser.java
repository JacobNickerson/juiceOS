package jbash.parser;

import jbash.filesystem.FileSystemAPI;

import java.util.*;

public final class JBashParser {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private String redirectTo = "";
    JBashParser(ArrayList<Token> tokens) {
        this.tokens.addAll(tokens);
    }

    /**
     * FIXME: Hacky implementation that will be replaced in its entirety later.
     *        This is only here so certain filesystem operations can be done, while
     *        we wait for the parser to be better-implemented.
     */
    private void processRedirects() {
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type() == TokenType.Great) {
                if (i + 1 >= tokens.size()) throw new RuntimeException("Expected token after redirect");

                Token next = tokens.get(i + 1);
                if (next.type() == TokenType.Whitespace) {
                    if (i + 2 >= tokens.size()) throw new RuntimeException("Expected token after redirect");
                    next = tokens.get(i + 2);
                }
                redirectTo = next.lexeme();
                tokens.remove(next);
                return;
            }
        }
    }

    public static ArrayList<String> parseCommand(String input) throws JBParserException {
        if (input.isEmpty()) { return new ArrayList<>(); }
        var parser = new JBashParser(JBashLexer.lexCommand(input));

        parser.processRedirects();

        if (!parser.redirectTo.isEmpty()) {

        }
        return new ArrayList<>(
                List.of(parser.tokens.parallelStream()
                        .map(Token::lexeme)
                        .reduce("", (s, t) -> s + t)
                        .split("\0")));
    }
}
