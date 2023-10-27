package sebfisch.expressions.data;

import java.util.function.Consumer;
import java.util.stream.Stream;

public sealed interface Expr {

    public sealed interface Const extends Expr permits Small, Num {
    }

    public sealed interface OpExpr extends Expr permits Unary, Binary {

        String op();
    }

    public sealed interface Unary extends OpExpr permits Neg {

        Expr nested();

        Expr withNested(Expr nested);
    }

    public sealed interface Binary extends OpExpr permits Add, Sub, Mul, Div {

        Expr left();

        Expr right();

        Expr withNested(Expr left, Expr right);
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
                "-%s".formatted(e.format());
            case Binary bin ->
                "(%s %s %s)".formatted(bin.left().format(), bin.op(), bin.right().format());
        };
    }

    default Stream<Expr> included() {
        return Stream.of(this).mapMulti(Expr::forEachIncluded);
    }

    default void forEachIncluded(final Consumer<Expr> action) {
        switch (this) {
            case Unary self -> {
                self.nested().forEachIncluded(action);
            }
            case Binary self -> {
                self.right().forEachIncluded(action);
                self.left().forEachIncluded(action);
            }
            default -> {
            }
        }
        action.accept(this);
    }
}
