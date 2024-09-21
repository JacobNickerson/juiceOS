package jbash;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class FileSystemTests {
    @Nested
    class GetFilePath {
        @ParameterizedTest
        @MethodSource
        public void testGetFilePath(String name, String command, Object expected) {
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
                            List.of("april", "and", "june")),
                    Arguments.of(
                            "No Words",
                            "",
                            List.of())
            );
}
