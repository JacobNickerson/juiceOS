package jbash.parser;

import jbash.jbashutils.JBashUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static jbash.jbashutils.JBashUtils.findFirstOf;

public class JBashLexer {
    private static final char escapedSentinel = '\uFDEF';
    private static final char[] specialChars =
            {'{', '}', '[', ']', '(', ')', '$', ' ',
                    '>', '"', '\'', '\\', '\n', escapedSentinel};
    private final StringBuilder str;  // Content string we are trying to parse
    private int start;                // Start of the current consumed lexeme
    private int current;              // Current character we are inspecting

    private final ArrayList<Token> tokens = new ArrayList<>();

    private static TokenType getKeywordType(String string) {
        return switch(string) {
            case "if"    -> TokenType.If;
            case "then"  -> TokenType.Then;
            case "else"  -> TokenType.Else;
            case "elif"  -> TokenType.Elif;
            case "fi"    -> TokenType.Fi;
            case "do"    -> TokenType.Do;
            case "done"  -> TokenType.Done;
            case "case"  -> TokenType.Case;
            case "esac"  -> TokenType.Esac;
            case "while" -> TokenType.While;
            case "until" -> TokenType.Until;
            case "for"   -> TokenType.For;
            default      -> TokenType.Word;
        };
    }


    JBashLexer(String str) {
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
    private Token nextToken() throws JBParserException {
        if (end()) { return new Token(TokenType.EOF, start, ""); }
        return switch(peek()) {
            case '\n' -> new Token(TokenType.EOF, start, "");
            case '\\' -> {
                consume();  // skip the value of this consume, go to next character
                var type = peek() == '\n'
                        ? TokenType.EOL
                        : TokenType.Word;
                yield new Token(type, start, consume());
            }
            case '>' -> new Token(TokenType.Great, start, consume());
            case escapedSentinel -> {
                if (!seek(escapedSentinel)) {
                    throw new JBParserException("Internal parser error: escaped region did not have final escape sentinel");
                }
                var content = consume();
                content = content.substring(1, content.length() - 1);
                yield new Token(TokenType.Word, start, content);
            }
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
                var word = consume();
                yield new Token(getKeywordType(word), start, word);
            }
        };
    }

    /**
     * Processes quotes in the input to prepare for tokenization.
     */
    private void processQuotes() throws JBParserException {
        // Each loop, we process a pair of quotes.
        int i = 0;  // Index of the last seen double quote.
        JBashUtils.FindResult quoteResult;
        while ((quoteResult = findFirstOf(String.valueOf(str), i, "'", "\"")).index() != -1) {
            var quote = quoteResult.str();
            int start = quoteResult.index();
            i = start + 1;

            // Escaped quotes aren't real, skip em
            if (start != 0 && str.charAt(start - 1) == '\\') {
                continue;
            }

            // loop until we find a non-escaped quote
            int end;
            while (true) {
                end = str.indexOf(quote, i);
                if (end == -1 || str.charAt(end - 1) != '\\') break;
                else i = end + 1;
            }
            if (end == -1) {
                // TODO: this should not throw an error, but rather begin a new line of input
                throw new JBParserException(JBParserException.msgNoMatching(quote.charAt(0), 1));
            }
            i = end + 1;

            // Pull out the content for processing, delete it from the original string
            var content = new StringBuilder(str.substring(start+1, end));
            str.delete(start+1, end);

            // Process that content, if our quote is "
            if (quote.equals("\"")) {
                int idx = 0;
                JBashUtils.FindResult dqResult;
                while ((dqResult = findFirstOf(String.valueOf(content), idx, "$", "`", "\\")).index() != -1) {
                    switch(dqResult.str()) {
                        case "$" -> {/*TODO $*/}
                        case "`" -> {/*TODO `*/}
                        case "\\" -> {
                            idx = dqResult.index()+1;
                            switch(content.charAt(idx)) {
                                case '$', '`', '"', '\\', '\n' -> content.deleteCharAt(idx - 1);
                                default -> {}
                            }
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + dqResult.str());
                    }
                }
            }

            // Place parsed string back where it belongs
            str.insert(start+1, content);
            str.setCharAt(start, escapedSentinel);  // This is our way of escaping the string contents
            str.setCharAt(start+content.length()+1, escapedSentinel);    // Since single-quoted strings content is always escaped
        }
    }

    /**
     * Breaks <code>str</code> into tokens.
     * This will populate `<code>this.tokens</code>.
     */
    private void processTokens() throws JBParserException {
        Token t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (t.type() != TokenType.EOF
                &&  t.type() != TokenType.EOL);
    }

    public static ArrayList<Token> lexCommand(String input) throws JBParserException {
        if (input.isEmpty()) { return new ArrayList<>(); }
        var lexer = new JBashLexer(input);

        lexer.processQuotes();
        lexer.processTokens();

        return lexer.tokens;
    }
}
