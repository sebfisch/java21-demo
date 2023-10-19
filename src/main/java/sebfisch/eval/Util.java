package sebfisch.eval;

public final class Util {

    private Util() {
        // static methods only
    }

    public static Expr simplify(Expr expr) {
        final Expr result = switch (expr.recursively(Util::simplify)) {
            // nested patterns can only be type patterns or record patterns, not case constants
            case Expr.Num(var value) when value == 0 ->
                Expr.Small.ZERO;
            case Expr.Num(var value) when value == 1 ->
                Expr.Small.ONE;
            case Expr.Add(var l, var r) when l == Expr.Small.ZERO ->
                r;
            case Expr.Add(var l, var r) when r == Expr.Small.ZERO ->
                l;

            case Expr.Mul e when e.left() == Expr.Small.ZERO || e.right() == Expr.Small.ZERO ->
                Expr.Small.ZERO;
            case Expr.Mul(var l, var r) when l == Expr.Small.ONE ->
                r;
            case Expr.Mul(var l, var r) when r == Expr.Small.ONE ->
                l;

            case Expr e ->
                e;
        };

        return result;
    }
}
