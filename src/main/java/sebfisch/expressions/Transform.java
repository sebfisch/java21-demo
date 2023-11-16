package sebfisch.expressions;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@FunctionalInterface
public interface Transform extends UnaryOperator<Expr> {

    static final Transform NO_CHANGE = e -> e;

    static Transform combineAll(Transform... transforms) {
        return Stream.of(transforms).reduce(NO_CHANGE, Transform::combine);
    }

    default Transform combine(Transform that) {
        return this.andThen(that)::apply;
    }

    default Transform recursively() {
        return expr -> apply(switch (expr) {
            case Expr.Unary e ->
                e.withNested(recursively().apply(e.nested()));
            case Expr.Binary e ->
                e.withNested(recursively().apply(e.left()), recursively().apply(e.right()));
            case Expr.Const e ->
                e;
        });
    }
}
