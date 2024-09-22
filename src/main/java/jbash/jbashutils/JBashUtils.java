package jbash.jbashutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JBashUtils {
    public record FindResult(String str, Integer index) {};

    /**
     * Searches <code>haystack</code> starting at <code>startIndex</code> for any occurrence
     * of a string in <code>needles</code> and returns the first match as a <code>FindResult</code>.
     * If no match exists, <code>("",-1)</code> is returned.
     *
     * @param haystack String to perform search in.
     * @param startIndex Index to begin the search from.
     * @param needles Strings to find in <code>haystack</code>.
     * @return FindResult of the first match, or <code>("", -1)</code> if none exist.
     */
    public static FindResult findFirstOf(String haystack, int startIndex, String... needles) {
        if (needles == null || needles.length == 0) {
            throw new RuntimeException("Improper use of findFirstOf: needles.length() must be greater than 0");
        }

        return List.of(needles)
                .parallelStream()
                .map(needle -> new FindResult(needle, haystack.indexOf(needle, startIndex)))
                .filter(result -> result.index != -1)
                .reduce(new FindResult("", -1), (a,b) -> a.index < b.index ? a : b);
    }
    // StringBuilder variant
    public static FindResult findFirstOf(StringBuilder haystack, int startIndex, String... needles) {
        return findFirstOf(String.valueOf(haystack), startIndex, needles);
    }
}
