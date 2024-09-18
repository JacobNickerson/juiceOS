package jbash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static jbash.parser.JBashParser.parseCommand;

public class ShellTests {

    @Nested
    class BasicParse {
        @ParameterizedTest
        @MethodSource
        public void testBasicParse(String name, String command, Object expected) {
            test(command, expected);
        }

        public static Stream<Arguments> testBasicParse() {
            return Stream.of(
                    Arguments.of(
                            "Single Word",              // Test case name
                            "april",                    // Input
                            List.of("april")),          // Expected output
                    Arguments.of(
                            "Multiple Words",
                            "april and june",
                            List.of("april", "and", "june"))
            );
        }
    }

    private static void test(String command, Object expected) {
        if (expected != null) {
            try {
                var result = parseCommand(command);
                Assertions.assertEquals(expected, result);
            } catch (Exception e) {
                Assertions.fail(e);
            }
        }
        // We use "null" as a value for when we expect a parse to fail.
        // Expect it to throw some kind of exception.
        else {
            Assertions.assertThrows(Exception.class, () -> {
                parseCommand(command);
            });
        }
    }

}
