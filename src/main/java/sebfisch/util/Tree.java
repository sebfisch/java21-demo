package sebfisch.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public interface Tree<T extends Tree<T>> {

    public interface Unary<T extends Tree<T>> extends Tree<T> {

        T child();

        T withChild(T child);
    }

    public interface Binary<T extends Tree<T>> extends Tree<T> {

        T left();

        T right();

        T withChildren(T left, T right);
    }

    default void forEachChild(final Consumer<T> action) {
        switch (this) {
            case Tree.Unary<T> self -> {
                action.accept(self.child());
            }
            case Tree.Binary<T> self -> {
                action.accept(self.left());
                action.accept(self.right());
            }
            default -> {
            }
        }
    }

    default Stream<T> children() {
        return Stream.of(this).mapMulti(Tree::forEachChild);
    }

    @SuppressWarnings("unchecked")
    default void forEachIncluded(final Consumer<T> action) {
        action.accept((T) this);
        forEachChild(child -> child.forEachIncluded(action));
    }

    default Stream<T> included() {
        return Stream.of(this).mapMulti(Tree::forEachIncluded);
    }

    @FunctionalInterface
    public interface Transform<T extends Tree<T>> extends UnaryOperator<T> {

        @SafeVarargs
        @SuppressWarnings("varargs")
        static <T extends Tree<T>> Transform<T> of(Transform<T>... transforms) {
            final Transform<T> noChange = tree -> tree;
            return Stream.of(transforms).reduce(noChange, Transform::before);
        }

        default Transform<T> before(Transform<T> that) {
            return this.andThen(that)::apply;
        }

        default Transform<T> onEveryChild() {
            return tree -> switch (tree) {
                case Tree.Unary<T> t ->
                    t.withChild(apply(t.child()));
                case Tree.Binary<T> t ->
                    t.withChildren(apply(t.left()), apply(t.right()));
                default ->
                    tree;
            };
        }

        default Transform<T> everywhere() {
            return expr -> apply(everywhere().onEveryChild().apply(expr));
        }
    }
}
