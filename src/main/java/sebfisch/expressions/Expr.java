package sebfisch.expressions;

import java.util.stream.IntStream;

import sebfisch.util.Partial;
import sebfisch.util.Tree;

public sealed interface Expr extends Tree<Expr> {

    public sealed interface Const extends Expr permits Small, Num {
    }

    public enum Small implements Const {
        ZERO, ONE
    }

    public record Num(int intValue) implements Const {

    }

    public sealed interface OpExpr extends Expr permits Neg, BinOpExpr {

        String op();
    }

    public record Neg(Expr child) implements OpExpr, Tree.Unary<Expr> {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Expr withChild(Expr child) {
            return new Neg(child);
        }
    }

    public sealed interface BinOpExpr extends OpExpr, Tree.Binary<Expr> permits Add, Sub, Mul, Div {
    }

    public record Add(Expr left, Expr right) implements BinOpExpr {

        @Override
        public String op() {
            return "+";
        }

        @Override
        public Expr withChildren(Expr left, Expr right) {
            return new Add(left, right);
        }
    }

    public record Sub(Expr left, Expr right) implements BinOpExpr {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Expr withChildren(Expr left, Expr right) {
            return new Sub(left, right);
        }
    }

    public record Mul(Expr left, Expr right) implements BinOpExpr {

        @Override
        public String op() {
            return "*";
        }

        @Override
        public Expr withChildren(Expr left, Expr right) {
            return new Mul(left, right);
        }
    }

    public record Div(Expr left, Expr right) implements BinOpExpr {

        @Override
        public String op() {
            return "/";
        }

        @Override
        public Expr withChildren(Expr left, Expr right) {
            return new Div(left, right);
        }
    }

    default int value() {
        return switch (this) {
            case Small.ZERO ->
                0;
            case Small.ONE ->
                1;
            case Num(var i) ->
                i;
            case Neg(var e) ->
                Math.negateExact(e.value());
            case Add(var l, var r) ->
                Math.addExact(l.value(), r.value());
            case Sub(var l, var r) ->
                Math.subtractExact(l.value(), r.value());
            case Mul(var l, var r) ->
                Math.multiplyExact(l.value(), r.value());
            case Div(var l, var r) ->
                Math.divideExact(l.value(), r.value());
        };
    }

    default Partial<Integer, ArithmeticException> partialValue() {
        try {
            return new Partial.Success<>(value());
        } catch (ArithmeticException e) {
            return new Partial.Failure<>(e);
        }
    }

    default String format() {
        return switch (this) {
            case Const e ->
                Integer.toString(e.value());
            case Neg(var e) ->
                "-%s".formatted(e.format());
            case BinOpExpr bin ->
                "(%s %s %s)".formatted(bin.left().format(), bin.op(), bin.right().format());
        };
    }

    default long size() {
        return included().filter(e -> e instanceof OpExpr).count();
    }

    default IntStream includedConstants() {
        return included()
                .filter(e -> e instanceof Expr.Const)
                .mapToInt(Expr::value);
    }
}
