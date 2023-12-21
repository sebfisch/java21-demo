package sebfisch.util.traversal;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@FunctionalInterface
public interface Transform<C> extends UnaryOperator<C> {

    static <P extends Has<P, C>, C> P children(P parent, Transform<C> tfm) {
        return switch (parent) {
            case Has.One/*<P,C>*/ p -> // parameterized type pattern handled incorrectly by Eclipse
                (P) p.withChild(tfm.apply((C) p.child())); // type cast required as a consequence
            case Has.Two/*<P,C>*/ p ->
                (P) p.withChildren(tfm.apply((C) p.left()), tfm.apply((C) p.right()));
            case Has.Any/*<P,C>*/ p ->
                (P) p.withChildren(p.children().stream().map(tfm).toList());
            default ->
                parent;
        };
    }

    static <P extends Has<P, C>, C extends Has<C, C>> P nested(P parent, Transform<C> tfm) {
        return Transform.children(parent, child -> Transform.all(child, tfm));
    }

    static <C extends Has<C, C>> C all(C parent, Transform<C> tfm) {
        return tfm.apply(Transform.nested(parent, tfm));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <C> Transform<C> of(Transform<C>... transforms) {
        final Transform<C> id = c -> c;
        return Stream.of(transforms).reduce(id, Transform::before);
    }

    default Transform<C> before(Transform<C> that) {
        return this.andThen(that)::apply;
    }

    default <P extends Has<P, C>> Transform<P> atChildren() {
        return parent -> Transform.children(parent, this);
    }
}
