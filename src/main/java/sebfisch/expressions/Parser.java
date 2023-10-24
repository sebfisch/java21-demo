package sebfisch.expressions;

import java.util.Scanner;
import java.util.function.BinaryOperator;

import sebfisch.expressions.data.Add;
import sebfisch.expressions.data.Div;
import sebfisch.expressions.data.Expr;
import sebfisch.expressions.data.Mul;
import sebfisch.expressions.data.Neg;
import sebfisch.expressions.data.Num;
import sebfisch.expressions.data.Sub;

public record Parser(Scanner input) {

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
            return new Num(input.nextInt());
        }
        if (input.hasNext("-")) {
            input.next(); // consume "-"
            return new Neg(parseExpr());
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
                Add::new;
            case "-" ->
                Sub::new;
            case "*" ->
                Mul::new;
            case "/" ->
                Div::new;
            default ->
                throw parseError();
        };
    }
}
