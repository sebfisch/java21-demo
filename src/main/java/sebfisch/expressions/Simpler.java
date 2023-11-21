package sebfisch.expressions;

import sebfisch.util.traversal.Query;
import sebfisch.util.traversal.Transform;

public final class Simpler {

    public static Expr expression(Expr expr) {
        return Transform.all(expr, Transform.of(
                Simpler::normalizeConst,
                Simpler::removeNeg,
                Simpler::removeNeutral,
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

    private static Expr cancelMul(Expr expr) {
        return switch (expr) {
            case Expr.Mul e when Query.children(e).anyMatch(Expr.Small.ZERO::equals) ->
                Expr.Small.ZERO;
            default ->
                expr;
        };
    }

    public static BoolExpr boolExpr(BoolExpr be) {
        return Transform.all(be, Transform.of(
                Simpler::withoutDoubleNot,
                Simpler::withoutOr, // may introduce double negation on grandchildren
                Transform.of(Simpler::withoutDoubleNot).<BoolExpr>atChildren().atChildren()
        ));
    }

    private static BoolExpr withoutDoubleNot(BoolExpr be) {
        return switch (be) {
            case BoolExpr.Not(BoolExpr.Not(var b)) ->
                b;
            default ->
                be;
        };
    }

    private static BoolExpr withoutOr(BoolExpr be) {
        return switch (be) {
            case BoolExpr.Or(var l, var r) ->
                new BoolExpr.Not(new BoolExpr.And(new BoolExpr.Not(l), new BoolExpr.Not(r)));
            default ->
                be;
        };
    }
}
