package sebfisch.eval;

import java.util.function.UnaryOperator;

public sealed interface Expr {

    public sealed interface Const extends Expr permits Small, Num {
    }

    public enum Small implements Const {
        ZERO, ONE
    }

    public record Num(int value) implements Const {

    }

    public sealed interface OpExpr extends Expr permits Unary, Binary {

        String op();
    }

    public sealed interface Unary extends OpExpr permits Neg {

        Expr nested();

        Expr withNested(Expr nested);
    }

    public record Neg(Expr nested) implements Unary {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Expr withNested(Expr nested) {
            return new Neg(nested);
        }
    }

    public sealed interface Binary extends OpExpr permits Add, Sub, Mul, Div {

        Expr left();

        Expr right();

        Expr withNested(Expr left, Expr right);
    }

    public record Add(Expr left, Expr right) implements Binary {

        @Override
        public String op() {
            return "+";
        }

        @Override
        public Expr withNested(Expr left, Expr right) {
            return new Add(left, right);
        }
    }

    public record Sub(Expr left, Expr right) implements Binary {

        @Override
        public String op() {
            return "-";
        }

        @Override
        public Expr withNested(Expr left, Expr right) {
            return new Sub(left, right);
        }
    }

    public record Mul(Expr left, Expr right) implements Binary {

        @Override
        public String op() {
            return "*";
        }

        @Override
        public Expr withNested(Expr left, Expr right) {
            return new Mul(left, right);
        }
    }

    public record Div(Expr left, Expr right) implements Binary {

        @Override
        public String op() {
            return "/";
        }

        @Override
        public Expr withNested(Expr left, Expr right) {
            return new Div(left, right);
        }
    }

    default int eval() {
        return switch (this) {
            case Small.ZERO ->
                0;
            case Small.ONE ->
                1;
            case Num(var value) ->
                value;
            case Neg(var e) ->
                -e.eval();
            case Add(var l, var r) ->
                l.eval() + r.eval();
            case Sub(var l, var r) ->
                l.eval() + r.eval();
            case Mul(var l, var r) ->
                l.eval() + r.eval();
            case Div(var l, var r) ->
                l.eval() + r.eval();
        };
    }

    default String format() {
        return switch (this) {
            case Small.ZERO ->
                "0";
            case Small.ONE ->
                "1";
            case Num(var value) ->
                Integer.toString(value);
            case Neg(var e) ->
                "-%s".formatted(e);
            case Binary bin ->
                "(%s %s %s)".formatted(bin.left(), bin.op(), bin.right());
        };
    }

    default Expr recursively(UnaryOperator<Expr> rewrite) {
        return switch (this) {
            case Unary e ->
                e.withNested(rewrite.apply(e.nested()));
            case Binary e ->
                e.withNested(rewrite.apply(e.left()), rewrite.apply(e.right()));
            default ->
                this;
        };
    }
}
