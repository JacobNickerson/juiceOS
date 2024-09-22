package jbash.jbashutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JBashUtils {
    public record FindResult(String str, Integer index) {};
    public static FindResult findFirstOf(String haystack, int startIndex, String... needles) {
        if (needles.length == 0) {
            throw new RuntimeException("Improper use of findFirstOf: needles.length() must be greater than 0");
        }

        return List.of(needles)
                .parallelStream()
                .map(needle -> new FindResult(needle, haystack.indexOf(needle, startIndex)))
                .filter(result -> result.index != -1)
                .reduce(new FindResult("", -1), (a,b) -> a.index < b.index ? a : b);
    }
}
