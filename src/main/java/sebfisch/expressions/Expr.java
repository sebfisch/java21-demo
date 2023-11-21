package sebfisch.expressions;

import java.util.stream.IntStream;

import sebfisch.util.Partial;
import sebfisch.util.traversal.Query;
import sebfisch.util.traversal.Rec;

public sealed interface Expr extends Rec<Expr> {

    sealed interface Const extends Expr permits Small, Num {
    }

    enum Small implements Const {
        ZERO, ONE
    }

    record Num(int intValue) implements Const {

    }

    sealed interface OpExpr extends Expr permits Neg, Bin {

        String op();
    }

    record Neg(Expr child) implements OpExpr, Rec.Unary<Expr> {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Neg withChild(Expr child) {
            return new Neg(child);
        }
    }

    sealed interface Bin extends OpExpr, Rec.Binary<Expr> permits Add, Sub, Mul, Div {
    }

    record Add(Expr left, Expr right) implements Bin {

        @Override
        public String op() {
            return "+";
        }

        @Override
        public Add withChildren(Expr left, Expr right) {
            return new Add(left, right);
        }
    }

    record Sub(Expr left, Expr right) implements Bin {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Sub withChildren(Expr left, Expr right) {
            return new Sub(left, right);
        }
    }

    record Mul(Expr left, Expr right) implements Bin {

        @Override
        public String op() {
            return "*";
        }

        @Override
        public Mul withChildren(Expr left, Expr right) {
            return new Mul(left, right);
        }
    }

    record Div(Expr left, Expr right) implements Bin {

        @Override
        public String op() {
            return "/";
        }

        @Override
        public Div withChildren(Expr left, Expr right) {
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
            case Bin bin ->
                "(%s %s %s)".formatted(bin.left().format(), bin.op(), bin.right().format());
        };
    }

    default long size() {
        return Query.all(this).filter(e -> e instanceof OpExpr).count();
    }

    default IntStream includedConstants() {
        return Query.all(this)
                .filter(e -> e instanceof Expr.Const)
                .mapToInt(Expr::value);
    }
}
