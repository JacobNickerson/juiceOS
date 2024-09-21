package jbash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static jbash.parser.JBashParser.parseCommand;

public class FileSystemTests {
    @Nested
    class GetFilePath {
        @ParameterizedTest
        @MethodSource
        public void testGetFilePath(String name, String command, Object expected) {
            test(command, expected);
        }

        public static Stream<Arguments> testGetFilePath() {
            return Stream.of(
                    Arguments.of(
                            "Single Word",              // Test case name
                            "april",                    // Input
                            List.of("april")),          // Expected output
                    Arguments.of(
                            "Multiple Words",
                            "april and june",
                            List.of("april", "and", "june")),
                    Arguments.of(
                            "No Words",
                            "",
                            List.of())
            );
        }

        private static void test(String input, Object expected) {
            if (expected != null) {
                try {
                    // Some file-system setup and test
                    var result = "TODO: result of some computation";
                    Assertions.assertEquals(expected, result);
                } catch (Exception e) {
                    Assertions.fail(e);
                }
            }
            // We use "null" as a value for when we expect a parse to fail.
            // Expect it to throw some kind of exception.
            else {
                Assertions.assertThrows(Exception.class, () -> {
                    // Some file system setup and test that we expect to throw an error
                });
            }
        }
    }
}
