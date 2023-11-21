package sebfisch.expressions;

import sebfisch.util.traversal.Query;
import sebfisch.util.traversal.Transform;

public final class Simpler {

    public static Expr expression(Expr expr) {
        return Transform.all(expr, Transform.inOrder(
                Simpler::normalizeConst,
                Simpler::cancelMul
        ));
    }

    private static Expr normalizeConst(Expr expr) {
        return switch (expr) {
            // nested patterns can only be type patterns or record patterns, not case constants
            case Expr.Num(var value) when value == 0 ->
                Expr.Small.ZERO;
            case Expr.Num(var value) when value == 1 ->
                Expr.Small.ONE;
            default ->
                expr;
        };
    }

    private static Expr cancelMul(Expr expr) {
        return switch (expr) {
            case Expr.Mul e when Query.children(e).anyMatch(Expr.Small.ZERO::equals) ->
                Expr.Small.ZERO;
            default ->
                expr;
        };
    }

    private Simpler() {
    }
}
