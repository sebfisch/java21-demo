package sebfisch.expressions.data;

public record Div(Expr left, Expr right) implements Expr.Binary {

    @Override
    public String op() {
        return "/";
    }

    @Override
    public Expr withNested(Expr left, Expr right) {
        return new Div(left, right);
    }
}
