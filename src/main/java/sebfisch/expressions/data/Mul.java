package sebfisch.expressions.data;

public record Mul(Expr left, Expr right) implements Expr.Binary {

    @Override
    public String op() {
        return "*";
    }

    @Override
    public Expr withNested(Expr left, Expr right) {
        return new Mul(left, right);
    }
}
