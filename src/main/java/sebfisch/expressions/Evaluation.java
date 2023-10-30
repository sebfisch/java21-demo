package sebfisch.expressions;

import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public final class Evaluation {

    private Evaluation() {
    }

    public sealed interface Result {

        enum Error implements Result {
            OVERFLOW, DIV_BY_ZERO
        }

        record Ok(int value) implements Result {

        }

        default Result flatMap(IntFunction<Result> fun) {
            return switch (this) {
                case Error e ->
                    e;
                case Ok(var value) ->
                    fun.apply(value);
            };
        }

        default Result map(IntUnaryOperator fun) {
            return flatMap(value -> new Result.Ok(fun.applyAsInt(value)));
        }
    }

    public static Result eval(Expr expr) {
        return switch (expr) {
            case Expr.Small.ZERO ->
                new Result.Ok(0);
            case Expr.Small.ONE ->
                new Result.Ok(1);
            case Expr.Num(var value) ->
                new Result.Ok(value);
            case Expr.Neg(var nested) -> {
                try {
                    yield eval(nested).map(Math::negateExact);
                } catch (ArithmeticException e) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Expr.Add(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.addExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Expr.Sub(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.subtractExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Expr.Mul(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.multiplyExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Expr.Div(var left, var right) ->
                eval(right).flatMap(r
                -> r == 0
                ? Result.Error.DIV_BY_ZERO
                : eval(left).flatMap(l
                -> l == Integer.MIN_VALUE && r == -1
                ? Result.Error.OVERFLOW
                : new Result.Ok(l / r)));
        };
    }
}
