package sebfisch.expressions;

import sebfisch.expressions.data.Add;
import sebfisch.expressions.data.Div;
import sebfisch.expressions.data.Expr;
import sebfisch.expressions.data.Mul;
import sebfisch.expressions.data.Neg;
import sebfisch.expressions.data.Num;
import sebfisch.expressions.data.Small;
import sebfisch.expressions.data.Sub;

public final class Simplification {

    private Simplification() {
    }

    public static final Transform TRANSFORM
            = Transform.combineAll(
                    Simplification::normalizeConst,
                    Simplification::removeNeg,
                    Simplification::removeNeutral,
                    Simplification::cancelMul
            ).recursively();

    private static Expr normalizeConst(Expr expr) {
        return switch (expr) {
            // nested patterns can only be type patterns or record patterns, not case constants
            case Num(var value) when value == 0 ->
                Small.ZERO;
            case Num(var value) when value == 1 ->
                Small.ONE;
            default ->
                expr;
        };
    }

    private static Expr removeNeg(Expr expr) {
        return switch (expr) {
            case Neg(Neg(var e)) ->
                e;
            case Add(var l, Neg(var r)) ->
                new Sub(l, r);
            case Sub(var l, Neg(var r)) ->
                new Add(l, r);
            default ->
                expr;
        };
    }

    private static Expr removeNeutral(Expr expr) {
        return switch (expr) {
            case Add(var l, var r) when l == Small.ZERO ->
                r;
            case Add(var l, var r) when r == Small.ZERO ->
                l;
            case Sub(var l, var r) when l == Small.ZERO ->
                new Neg(r);
            case Sub(var l, var r) when r == Small.ZERO ->
                l;
            case Mul(var l, var r) when l == Small.ONE ->
                r;
            default ->
                expr;
        };
    }

    private static Expr cancelMul(Expr expr) {
        return switch (expr) {
            case Mul e when e.left() == Small.ZERO || e.right() == Small.ZERO ->
                Small.ZERO;
            case Div e when e.left() == Small.ZERO ->
                Small.ZERO;
            default ->
                expr;
        };
    }
}
