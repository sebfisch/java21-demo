package sebfisch.io;

import java.io.IOException;
import java.util.function.Consumer;

public sealed interface IO<T> {

    record Result<T>(T value) implements IO<T> {

    }

    record Error<T>(IOException exception) implements IO<T> {

    }

    default void ifResult(Consumer<T> consumer) {
        if (this instanceof Result(var value)) {
            consumer.accept(value);
        }
    }

    default void ifError(Consumer<IOException> consumer) {
        if (this instanceof Error(var exception)) {
            consumer.accept(exception);
        }
    }
}
