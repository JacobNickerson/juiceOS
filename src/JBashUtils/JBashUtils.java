package JBashUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class JBashUtils {
    /**
     * TODO: Add docs
     * @param input takes
     * @return
     */
    public static ArrayList<String> parseCommand(String input) {
        return new ArrayList<>(
                Arrays.asList(input.split("\\s+"))
        );
    }

}
