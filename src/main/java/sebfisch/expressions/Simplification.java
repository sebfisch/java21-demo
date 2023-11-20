package sebfisch.expressions;

import sebfisch.util.Tree;

public final class Simplification {

    private Simplification() {
    }

    public static final Tree.Transform<Expr> OF_EXPR
            = Tree.Transform.of(
                    Simplification::normalizeConst,
                    Simplification::cancelMul
            ).everywhere();

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
            case Expr.Mul e when e.children().anyMatch(Expr.Small.ZERO::equals) ->
                Expr.Small.ZERO;
            default ->
                expr;
        };
    }
}
