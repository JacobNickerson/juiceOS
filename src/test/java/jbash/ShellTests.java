package jbash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static jbash.parser.JBashParser.parseCommand;

/**
 * Test suite ensuring the functionality of the Shell's parsing.
 * Assumes a parser with signature String -> ArrayList of String.
 */
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
                            List.of("april", "and", "june")),
                    Arguments.of(
                            "No Words",
                            "",
                            List.of())
            );
        }
    }

    @Nested
    class SingleQuotes {
        @ParameterizedTest
        @MethodSource
        public void testSingleQuotes(String name, String command, Object expected) {
            test(command, expected);
        }

        public static Stream<Arguments> testSingleQuotes() {
            return Stream.of(
                    Arguments.of(
                            "Single Quotes Basic",
                            "'april'",
                            List.of("april")),
                    Arguments.of(
                            "Single Quotes Multiple Words I",
                            "'april and june'",
                            List.of("april and june")),
                    Arguments.of(
                            "Single Quotes Multiple Words II",
                            "'april' and 'june'",
                            List.of("april", "and", "june")),
                    Arguments.of(
                            "Single Quotes Double Quotes",
                            "'april \"and\" june'",  // 'april "and" june'
                            List.of("april \"and\" june")),
                    Arguments.of(
                            "Single Quotes Concatenation",
                            "'april'and'june'",
                            List.of("aprilandjune")),
                    Arguments.of(
                            "Single Quotes Escapes",  // Single quotes do not support escaping!
                            "'apr\\'il",  // 'apr\'il
                            List.of("april")),
                    Arguments.of(
                            "Single Quotes Escapes II",
                            "'april\\june'",
                            List.of("april\\june")),
                    Arguments.of(
                            "Single Quotes Escapes III",
                            "'april\\t  \\june'",
                            List.of("april\\t  \\june"))
            );
        }
    }

    @Nested
    class DoubleQuotes {
        @ParameterizedTest
        @MethodSource
        public void testDoubleQuotes(String name, String command, Object expected) {
            test(command, expected);
        }

        public static Stream<Arguments> testDoubleQuotes() {
            return Stream.of(
                    Arguments.of(
                            "Double Quotes Basic",
                            "\"april\"",  // "april"
                            List.of("april")),
                    Arguments.of(
                            "Double Quotes Multiple Words",
                            "\"april and may\"",
                            List.of("april and may")),
                    Arguments.of(
                            "Double Quotes Multiple Words II",
                            "\"april\" and \"may\"",
                            List.of("april", "and", "may")),
                    Arguments.of(
                            "Double Quotes Concatenation",
                            "\"april\"and\"may\"",
                            List.of("aprilandmay")),
                    Arguments.of(
                            "Double Quotes Concatenation",
                            "\"april\"and\"may\"",
                            List.of("aprilandmay")),
                    Arguments.of(
                            "Double Quotes with Single Quotes",
                            "\"april, or could it be 'may'?\"",
                            List.of("april, or could it be 'may'?")),

                    /* POSIX TEST (2.2.3):
                    The <backslash> shall retain its special meaning as an escape character,
                    only when followed by one of the following characters when considered special:
                        $   `   "   \   <newline>
                    */
                    Arguments.of(
                            "Double Quotes Escaped Dollar",
                            "\"\\$EAL\"",
                            List.of("$EAL")),
                    Arguments.of(
                            "Double Quotes Escaped Backtick",
                            "\"\\`backtick'd\\`\"",
                            List.of("`backtick'd`")),
                    Arguments.of(
                            "Double Quotes Escaped Quote",
                            "\"april \\\"and\\\" may\"",  // "april \"and\" may"
                            List.of("april \"and\" may")), // [april "and" may]
                    Arguments.of(
                            "Double Quotes Escaped Backslash",
                            "\"april\\\\may\"",  // "april\\may"
                            List.of("april\\may")),  // [april\may]
                    Arguments.of(
                            "Double Quotes Escaped Newline",
                            "\"april and...\\\nmay\"",  // "april and...\
                                                        // may"
                            List.of("april and...may")),
                    Arguments.of(
                            "Double Quotes Only Aforementioned Characters",
                            "\"\\a\\b\\c\\d\\e\\f\\g\"",  // "a\b\c\d\e\f\g"
                            List.of("\\a\\b\\c\\d\\e\\f\\g")),
                    Arguments.of(
                            "Double Quotes Only Aforementioned Characters II",
                            "\"C:\\Program Files (x86)\\Java\\jdk1.8.0_271\\jre\\bin\"",
                            List.of("C:\\Program Files (x86)\\Java\\jdk1.8.0_271\\jre\\bin"))
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
