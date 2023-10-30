package sebfisch.expressions;

public final class Simplification {

    private Simplification() {
    }

    public static final Transform TRANSFORM
            = Transform.combineAll(
                    Simplification::normalizeConst,
                    Simplification::removeNeg,
                    Simplification::removeNeutral
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

    private static Expr removeNeg(Expr expr) {
        return switch (expr) {
            case Expr.Neg(Expr.Neg(var e)) ->
                e;
            case Expr.Add(var l, Expr.Neg(var r)) ->
                new Expr.Sub(l, r);
            case Expr.Sub(var l, Expr.Neg(var r)) ->
                new Expr.Add(l, r);
            default ->
                expr;
        };
    }

    private static Expr removeNeutral(Expr expr) {
        return switch (expr) {
            case Expr.Add(var l, var r) when l == Expr.Small.ZERO ->
                r;
            case Expr.Add(var l, var r) when r == Expr.Small.ZERO ->
                l;
            case Expr.Sub(var l, var r) when l == Expr.Small.ZERO ->
                new Expr.Neg(r);
            case Expr.Sub(var l, var r) when r == Expr.Small.ZERO ->
                l;
            case Expr.Mul(var l, var r) when l == Expr.Small.ONE ->
                r;
            default ->
                expr;
        };
    }
}
