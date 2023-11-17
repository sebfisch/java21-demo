package sebfisch.expressions;

public final class Simplification {

    private Simplification() {
    }

    public static final Transform TRANSFORM
            = Transform.combineAll(
                    Simplification::normalizeConst,
                    Simplification::cancelMul
            ).recursively();

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
            case Expr.Mul e when e.left() == Expr.Small.ZERO || e.right() == Expr.Small.ZERO ->
                Expr.Small.ZERO;
            default ->
                expr;
        };
    }
}
