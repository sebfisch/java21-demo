package sebfisch.expressions;

import java.util.List;
import java.util.Scanner;
import java.util.function.BinaryOperator;

public record Parser(Scanner input) {

    public static StringTemplate.Processor<Expr, IllegalArgumentException> EXPR = template -> {
        List<Object> invalidValues = template.values()
                .stream()
                .filter(value -> !(value instanceof Integer))
                .toList();
        if (!invalidValues.isEmpty()) {
            throw new IllegalArgumentException("invalid values: %s".formatted(invalidValues));
        }
        final String input = StringTemplate.interpolate(template.fragments(), template.values());
        return new Parser(input).parseExpression();
    };

    public Parser(String input) {
        this(new Scanner(input));
    }

    public Parser {
        input.useDelimiter("\\s*(?=[-+()*/])|(?<=[-+()*/])\\s*");
    }

    public Expr parseExpression() {
        final Expr result = parseExpr();
        if (input.hasNext()) { // unread input remains
            throw parseError();
        }
        return result;
    }

    private IllegalStateException parseError() {
        return new IllegalStateException("cannot parse %s".formatted(input));
    }

    private Expr parseExpr() {
        if (input.hasNextInt()) {
            return new Expr.Num(input.nextInt());
        }
        if (input.hasNext("-")) {
            input.next(); // consume "-"
            return new Expr.Neg(parseExpr());
        }
        if (input.hasNext("\\(")) {
            input.next(); // consume "("
            Expr left = parseExpr();
            BinaryOperator<Expr> bin = parseOp();
            Expr right = parseExpr();
            input.next(); // consume ")"
            return bin.apply(left, right);
        }
        throw parseError();
    }

    private BinaryOperator<Expr> parseOp() {
        return switch (input.next()) {
            case "+" ->
                Expr.Add::new;
            case "-" ->
                Expr.Sub::new;
            case "*" ->
                Expr.Mul::new;
            case "/" ->
                Expr.Div::new;
            default ->
                throw parseError();
        };
    }
}
