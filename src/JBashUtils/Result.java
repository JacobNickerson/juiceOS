package JBashUtils;

// The sealed interface representing the result of parsing
public sealed interface Result<T> permits Result.Ok, Result.Err {
    record Ok<T>(T value) implements Result<T> {}
    record Err<T>(String errorMessage) implements Result<T> {}
}