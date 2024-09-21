package jbash.parser;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class JBashParser {
    private static final char[] specialChars =
            {'{', '}', '[', ']', '(', ')', '$', ' '};
    private final StringBuilder str;  // Content string we are trying to parse
    private int start;                // Start of the current consumed lexeme
    private int current;              // Current character we are inspecting


    JBashParser(String str) {
        this.str = new StringBuilder(str);
        this.start = 0;
        this.current = 0;
    }


    /**
     * Returns whether we are at the end of the current string.
     */
    private boolean end() {
        return current == str.length();
    }


    private char prev() {
        return str.charAt(current - 1);
    }


    /**
     * Returns the current character.
     */
    private char peek() {
        return str.charAt(current);
    }

    /**
     * Skips to the end of the input.
     */
    private void skip() {
        current = str.length();
    }


    /**
     * Increments current until it finds target.
     * Returns false if unsuccessful, and reverts current to before method call.
     */
    private boolean seek(char... targets) {
        int originalCurrent = current;
        do {
            current++;
        } while (!end() && Arrays.toString(targets).indexOf(peek()) == -1);

        // Couldn't find anything
        if (end()) {
            current = originalCurrent;
            return false;
        }

        return true;
    }


    /**
     * Increments current until it finds non-escaped target.
     * Returns false if unsuccessful, and reverts current to before method call.
     */
    private boolean seekNotEscaped(char... targets) {
        int originalCurrent = current;
        do {
            current++;
        } while (!end() && (prev() == '\\' || Arrays.toString(targets).indexOf(peek()) == -1));

        // Couldn't find anything
        if (end()) {
            current = originalCurrent;
            return false;
        }

        return true;
    }


    /**
     * Increments current until it finds target. Current will be one index before target.
     * Returns false if unsuccessful, and reverts current to before method call.
     */
    private boolean seekUntil(char... targets) {
        if (!seek(targets)) {
            return false;
        }

        current--;  // Current will be one index before target.
        return true;
    }


    /**
     * Increments current until it finds non-escaped target. Current will be one index before target.
     * Returns false if unsuccessful, and reverts current to before method call.
     */
    private boolean seekUntilNotEscaped(char... targets) {
        if (!seekNotEscaped(targets)) {
            return false;
        }

        current--;  // Current will be one index before target.
        return true;
    }


    /**
     * Attempts to match and consume any of `matches` with
     * the current character being pointed at.
     */
    private boolean match(char... matches) {
        if (end()) return false;
        for (var c : matches) {
            if (peek() == c) {
                current++;
                return true;
            }
        }
        return false;
    }


    /**
     * Consumes the current lexeme from start to current, including current.
     * Increments current and resets start to current.
     */
    private String consume() {
        current += end() ? 0 : 1;  // don't increment if already at end
        String lexeme = new String(str).substring(start, current);

        start = current;
        return lexeme;
    }


    /**
     * Retrieves the next token from the parser and moves the internal pointer.
     * Will return a token of TokenKind.EOF if no more tokens are available.
     * @return next Token in input stream
     */
    public Token nextToken() throws JBParserException {
        if (end()) return new Token(TokenType.EOF, start, "");
        return switch(peek()) {
            case ' ', '\t' -> {
                // Keep going until no whitespace
                while (match(' ', '\t'));

                current--;
                consume();
                yield new Token(TokenType.Whitespace, start, "\0");
            }
            case '$' -> new Token(TokenType.Dollar, start, consume());
            case '\'' -> {
                if (!seek('\'')) {
                    throw new JBParserException(JBParserException.msgNoMatching('\'', current));
                }
                var content = consume();
                content = content.substring(1, content.length() - 1);  // peel off those quotes
                yield new Token(TokenType.StringLit, start, content);
            }
            case '(' -> {
                if (!seek(')')) {
                    throw new JBParserException(JBParserException.msgNoMatching(')', current));
                }
                yield new Token(TokenType.ParenExpr, start, consume());
            }
            case '{' -> {
                if (!seek('}')) {
                    throw new JBParserException(JBParserException.msgNoMatching('}', current));
                }
                yield new Token(TokenType.CurlyExpr, start, consume());
            }
            case '[' -> {
                if (!seek(']')) {
                    throw new JBParserException(JBParserException.msgNoMatching(']', current));
                }
                yield new Token(TokenType.BrackExpr, start, consume());
            }

            // Characters that shouldn't be used on their own
            case ')', '}', ']' -> { throw new JBParserException("Unexpected token "+peek()); }
            default -> {
                if (!seekUntilNotEscaped(specialChars)) {
                    skip();
                }
                yield new Token(TokenType.Word, start, consume());
            }
        };
    }

    /**
     * Processes quotes in the input to prepare for tokenization.
     */
    private void processQuotes() throws JBParserException {
        // Each loop, we process a pair of double quotes.
        int i = 0;  // Index of the last seen double quote.
        while (str.indexOf("\"", i) != -1) {
            int start = str.indexOf("\"", i);
            i = start + 1;

            // Escaped quotes aren't real, skip em
            if (start != 0 && str.charAt(start - 1) == '\\') {
                continue;
            }
            int end = str.indexOf("\"", i);
            if (end == -1) {
                // TODO: this should not throw an error, but rather begin a new line of input
                throw new JBParserException(JBParserException.msgNoMatching('"', 1));
            }
            i = end + 1;

            // Pull out the content for processing, delete it from the original string
            var content = str.substring(start+1, end);
            str.delete(start+1, end);

            // Process that content
            // todo: something

            // Place parsed string back where it belongs
            str.insert(start+1, content);
            str.setCharAt(start, '\'');  // This is our way of escaping the string contents
            str.setCharAt(end, '\'');    // Since single-quoted strings content is always escaped
        }
    }

    public static ArrayList<String> parseCommand(String input) throws JBParserException {
        var parser = new JBashParser(input);
        parser.processQuotes();
        ArrayList<Token> tokens = new ArrayList<>();
        do {
            var t = parser.nextToken();
            tokens.add(t);
        } while (tokens.getLast().type() != TokenType.EOF);

        return new ArrayList<>(
                List.of(tokens.parallelStream()
                        .map(Token::lexeme)
                        .reduce("", (s, t) -> s + t)
                        .split("\0")));
    }

}
