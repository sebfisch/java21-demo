package sebfisch.io;

import java.io.IOException;
import java.util.function.Consumer;

public sealed interface IO<T> {

    record Result<T>(T value) implements IO<T> {

    }

    record Failure<T>(IOException exception) implements IO<T> {

    }

    default void onSuccess(Consumer<T> consumer) {
        if (this instanceof Result(var value)) {
            consumer.accept(value);
        }
    }

    default void onFailure(Consumer<IOException> consumer) {
        if (this instanceof Failure(var exception)) {
            consumer.accept(exception);
        }
    }

    default void printErrorMessage() {
        onFailure(e -> System.err.println(e.getMessage()));
    }
}
