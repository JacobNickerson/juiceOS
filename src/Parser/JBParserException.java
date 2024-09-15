package Parser;

public class JBParserException extends Exception {
    public static String msgNoMatching(char charToMatch, int indexOfFirst) {
        return "at: " + indexOfFirst + " No matching '"+charToMatch+"' found";
    }
    public JBParserException(String message) {
        super("Parse Error:" + message);
    }
}
