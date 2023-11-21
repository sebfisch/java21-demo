package sebfisch.util.traversal;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@FunctionalInterface
public interface Transform<C> extends UnaryOperator<C> {

    public static <P extends Has<P, C>, C> P children(P parent, Transform<C> op) {
        return switch (parent) {
            case Has.One<P, C> p ->
                p.withChild(op.apply(p.child()));
            case Has.Two<P, C> p ->
                p.withChildren(op.apply(p.left()), op.apply(p.right()));
            case Has.Any<P, C> p ->
                p.withChildren(p.children().stream().map(op).toList());
            default ->
                parent;
        };
    }

    public static <P extends Has<P, C>, C extends Has<C, C>> P nested(P parent, Transform<C> op) {
        return Transform.children(parent, child -> Transform.all(child, op));
    }

    public static <C extends Has<C, C>> C all(C parent, Transform<C> op) {
        return op.apply(Transform.nested(parent, op));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <C> Transform<C> of(Transform<C>... transforms) {
        final Transform<C> id = c -> c;
        return Stream.of(transforms).reduce(id, Transform::before);
    }

    default Transform<C> before(Transform<C> that) {
        return c -> this.andThen(that).apply(c);
    }

    default <P extends Has<P, C>> Transform<P> atChildren() {
        return parent -> Transform.children(parent, this);
    }
}
