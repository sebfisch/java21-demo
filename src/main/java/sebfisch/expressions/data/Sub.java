package sebfisch.expressions.data;

public record Sub(Expr left, Expr right) implements Expr.Binary {

    @Override
    public String op() {
        return "-";
    }

    @Override
    public Expr withNested(Expr left, Expr right) {
        return new Sub(left, right);
    }
}
