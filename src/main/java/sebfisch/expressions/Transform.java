package sebfisch.expressions;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@FunctionalInterface
public interface Transform extends UnaryOperator<Expr> {

    static final Transform NO_CHANGE = e -> e;

    static Transform inOrder(Transform... transforms) {
        return Stream.of(transforms).reduce(NO_CHANGE, Transform::before);
    }

    default Transform before(Transform that) {
        return this.andThen(that)::apply;
    }

    default Transform onEveryChild() {
        return expr -> switch (expr) {
            case Expr.Unary e ->
                e.withChild(apply(e.child()));
            case Expr.Binary e ->
                e.withChildren(apply(e.left()), apply(e.right()));
            case Expr.Const e ->
                e;
        };
    }

    default Transform everywhere() {
        return expr -> apply(everywhere().onEveryChild().apply(expr));
    }
}
