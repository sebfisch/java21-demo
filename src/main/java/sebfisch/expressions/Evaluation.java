package sebfisch.expressions;

import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

import sebfisch.expressions.data.Add;
import sebfisch.expressions.data.Div;
import sebfisch.expressions.data.Expr;
import sebfisch.expressions.data.Mul;
import sebfisch.expressions.data.Neg;
import sebfisch.expressions.data.Num;
import sebfisch.expressions.data.Small;
import sebfisch.expressions.data.Sub;

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
            case Small.ZERO ->
                new Result.Ok(0);
            case Small.ONE ->
                new Result.Ok(1);
            case Num(var value) ->
                new Result.Ok(value);
            case Neg(var nested) -> {
                try {
                    yield eval(nested).map(Math::negateExact);
                } catch (ArithmeticException e) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Add(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.addExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Sub(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.subtractExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Mul(var left, var right) -> {
                try {
                    yield eval(left).flatMap(l -> eval(right).map(r -> Math.multiplyExact(l, r)));
                } catch (ArithmeticException ex) {
                    yield Result.Error.OVERFLOW;
                }
            }
            case Div(var left, var right) ->
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
