package sebfisch.expressions;

import sebfisch.util.Tree;

public final class Simplification {

    private Simplification() {
    }

    public static final Tree.Transform<Expr> OF_EXPR
            = Tree.Transform.of(
                    Simplification::normalizeConst,
                    Simplification::removeNeg,
                    Simplification::removeNeutral,
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
            case Expr.Mul e when e.children().anyMatch(Expr.Small.ZERO::equals) ->
                Expr.Small.ZERO;
            default ->
                expr;
        };
    }

    public static final Tree.Transform<BoolExpr> OF_BOOL_EXPR = Tree.Transform.inOrder(
            Simplification::withoutOr, // might introduce double negation on grandchildren
            Tree.Transform.inOrder(Simplification::withoutDoubleNot).onEveryChild().onEveryChild(),
            Simplification::withoutDoubleNot
    ).everywhere();

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
