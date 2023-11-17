package sebfisch.expressions;

import static java.lang.StringTemplate.STR;
import java.util.function.Consumer;
import java.util.stream.Stream;

public sealed interface Expr {

    public sealed interface Const extends Expr permits Small, Num {
    }

    public enum Small implements Const {
        ZERO, ONE
    }

    public record Num(int intValue) implements Const {

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

    default int value() {
        return switch (this) {
            case Small.ZERO ->
                0;
            case Small.ONE ->
                1;
            case Num(var i) ->
                i;
            case Neg(var e) ->
                -e.value();
            case Add(var l, var r) ->
                l.value() + r.value();
            case Sub(var l, var r) ->
                l.value() - r.value();
            case Mul(var l, var r) ->
                l.value() * r.value();
            case Div(var l, var r) ->
                l.value() / r.value();
        };
    }

    default String format() {
        return switch (this) {
            case Const e ->
                Integer.toString(e.value());
            case Neg(var e) ->
                STR."-\{e.format()}";
            case Binary bin ->
                STR."(\{bin.left().format()} \{bin.op()} \{bin.right().format()})";
        };
    }

    default Stream<Expr> included() {
        return Stream.of(this).mapMulti(Expr::forEachIncluded);
    }

    default void forEachIncluded(final Consumer<Expr> action) {
        action.accept(this);
        switch (this) {
            case Unary self -> {
                self.nested().forEachIncluded(action);
            }
            case Binary self -> {
                self.left().forEachIncluded(action);
                self.right().forEachIncluded(action);
            }
            default -> {
            }
        }
    }

    default long size() {
        return included().filter(e -> e instanceof OpExpr).count();
    }
}
