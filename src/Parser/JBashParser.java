package Parser;

import java.util.ArrayList;
import java.util.Arrays;

public class JBashParser {
    /**
     * TODO: Add docs
     * @param input takes
     * @return
     */
    public static ArrayList<Token> parseCommand(String input) {
        return new ArrayList<>(
                Arrays.asList(input.split("\\s+"))
        );
    }
}
