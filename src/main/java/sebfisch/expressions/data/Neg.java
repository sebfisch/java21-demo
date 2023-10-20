package sebfisch.expressions.data;

public record Neg(Expr nested) implements Expr.Unary {

    @Override
    public String op() {
        return "-";
    }

    @Override
    public Expr withNested(Expr nested) {
        return new Neg(nested);
    }
}
