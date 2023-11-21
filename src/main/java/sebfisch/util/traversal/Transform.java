package sebfisch.util.traversal;

import java.util.function.Function;
import java.util.stream.Stream;

public final class Transform {

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <C> Function<C, C> inOrder(Function<C, C>... ops) {
        return Stream.of(ops).reduce(Function.identity(), Function::andThen);
    }

    public static <P extends Has<P, C>, C> P children(P parent, Function<C, C> op) {
        return switch (parent) {
            case Has.One<P, C> p ->
                p.withChild(op.apply(p.child()));
            case Has.Two<P, C> p ->
                p.withChildren(op.apply(p.left()), op.apply(p.right()));
            default ->
                parent;
        };
    }

    public static <P extends Has<P, C>, C extends Has<C, C>> P nested(P parent, Function<C, C> op) {
        return Transform.children(parent, child -> Transform.all(child, op));
    }

    public static <C extends Has<C, C>> C all(C parent, Function<C, C> op) {
        return op.apply(Transform.nested(parent, op));
    }

    private Transform() {
    }
}
