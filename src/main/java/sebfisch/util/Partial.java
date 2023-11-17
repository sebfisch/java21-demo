package sebfisch.util;

import java.util.function.Consumer;

public sealed interface Partial<R, E> {

    record Success<R, E>(R result) implements Partial<R, E> {

    }

    record Failure<R, E>(E error) implements Partial<R, E> {

    }

    default void ifSuccess(Consumer<R> action) {
        if (this instanceof Success(var result)) {
            action.accept(result);
        }
    }

    default void ifFailure(Consumer<E> action) {
        if (this instanceof Failure(var error)) {
            action.accept(error);
        }
    }
}
