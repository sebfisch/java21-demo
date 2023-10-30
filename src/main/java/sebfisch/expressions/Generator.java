package sebfisch.expressions;

import java.util.SplittableRandom;
import java.util.function.BinaryOperator;

public record Generator(SplittableRandom random) {

    private static final int SIZE_LIMIT = 100;
    private static final int NUM_LIMIT = 100;

    public static void main(String[] args) {
        System.out.println(new Generator().randomExpr().format());
    }

    public Generator() {
        this(new SplittableRandom());
    }

    public Expr randomExpr() {
        return randomExpr(random.nextInt(SIZE_LIMIT));
    }

    public Expr randomExpr(int size) {
        if (size == 0) {
            return new Expr.Num(random.nextInt(0, NUM_LIMIT));
        }

        final int exprType = random.nextInt(5);

        if (exprType == 0) {
            return new Expr.Neg(randomExpr(size - 1));
        }

        BinaryOperator<Expr> bin = switch (exprType) {
            case 1 ->
                Expr.Add::new;
            case 2 ->
                Expr.Sub::new;
            case 3 ->
                Expr.Mul::new;
            case 4 ->
                Expr.Div::new;
            default ->
                throw new IllegalStateException("exprType is between 1 and 4");
        };

        final int leftSize = random.nextInt(size);
        final int rightSize = size - 1 - leftSize;

        return bin.apply(randomExpr(leftSize), randomExpr(rightSize));
    }
}
