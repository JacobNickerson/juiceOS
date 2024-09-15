package Parser;

import java.util.*;

public final class JBashParser {
    private static final char[] specialChars =
            {'{', '}', '[', ']', '(', ')', '$', ' '};
    private final char[] str;   // Content string we are trying to parse
    private int start;          // Start of the current consumed lexeme
    private int current;        // Current character we are inspecting


    JBashParser(String str) {
        this.str = str.toCharArray();
        this.start = 0;
        this.current = 0;
    }


    private boolean end() {
        return current == str.length;
    }

    private char prev() {
        return str[current - 1];
    }

    /**
     * Returns the current character.
     */
    private char peek() {
        return str[current];
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
            //current = originalCurrent;
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
            //current = originalCurrent;
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
        for (var c : matches) {
            if (peek() == c) {
                current++;
                return true;
            }
        }
        return false;
    }


    // Consumes the current lexeme from start to current, not including current.
    // Increments the current pointer.
    private String consume() {
        var lexeme = new String(str).substring(start, ++current);
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
                yield nextToken();
            }
            case '$' -> new Token(TokenType.Dollar, start, consume());
            case '"', '\'' -> {
                if (!seekNotEscaped(peek())) {
                    throw new JBParserException(JBParserException.msgNoMatching(peek(), current));
                }
                var type = peek() == '"' ? TokenType.StringFormat : TokenType.StringLit;
                yield new Token(type, start, consume());
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
                    current--;  // This helps ward off out-of-bound exceptions
                }
                yield new Token(TokenType.Word, start, consume());
            }
        };
    }


    public static ArrayList<Token> parseCommand(String input) throws JBParserException {
        var parser = new JBashParser(input);
        ArrayList<Token> tokens = new ArrayList<>();
        while (true) {
            var t = parser.nextToken();
            tokens.add(t);
            if (t.type() == TokenType.EOF) {
                return tokens;
            }
        }
    }
}
