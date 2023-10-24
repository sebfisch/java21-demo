package sebfisch.expressions;

import java.util.SplittableRandom;
import java.util.function.BinaryOperator;

import sebfisch.expressions.data.Add;
import sebfisch.expressions.data.Div;
import sebfisch.expressions.data.Expr;
import sebfisch.expressions.data.Mul;
import sebfisch.expressions.data.Neg;
import sebfisch.expressions.data.Num;
import sebfisch.expressions.data.Sub;

public record Generator(SplittableRandom random) {

    private static final int SIZE_LIMIT = 100;
    private static final int NUM_LIMIT = 100;

    public static void main(String[] args) {
        final SplittableRandom random = new SplittableRandom();
        System.out.println(new Generator(random).randomExpr(random.nextInt(SIZE_LIMIT)).format());
    }

    private Expr randomExpr(int size) {
        if (size == 0) {
            return new Num(random.nextInt(0, NUM_LIMIT));
        }

        final int exprType = random.nextInt(5);

        if (exprType == 0) {
            return new Neg(randomExpr(size - 1));
        }

        BinaryOperator<Expr> bin = switch (exprType) {
            case 1 ->
                Add::new;
            case 2 ->
                Sub::new;
            case 3 ->
                Mul::new;
            case 4 ->
                Div::new;
            default ->
                throw new IllegalStateException("exprType is between 1 and 4");
        };

        final int leftSize = random.nextInt(size);
        final int rightSize = size - 1 - leftSize;

        return bin.apply(randomExpr(leftSize), randomExpr(rightSize));
    }
}
