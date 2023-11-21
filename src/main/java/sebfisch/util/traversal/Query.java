package sebfisch.util.traversal;

import java.util.stream.Stream;

public final class Query {

    public static <P extends Has<P, C>, C> Stream<C> children(P parent) {
        return Stream.of(parent).mapMulti(Traverse::children);
    }

    public static <P extends Has<P, C>, C extends Has<C, C>> Stream<C> nested(P parent) {
        return Stream.of(parent).mapMulti(Traverse::nested);
    }

    public static <C extends Has<C, C>> Stream<C> all(C parent) {
        return Stream.of(parent).mapMulti(Traverse::all);
    }

    private Query() {
    }
}
