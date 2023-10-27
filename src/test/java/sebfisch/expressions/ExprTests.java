package sebfisch.expressions;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import sebfisch.expressions.data.Expr;

class ExprTests {

    private static final Generator GEN = new Generator();

    public static Stream<Expr> randomExpression() {
        return Stream.generate(GEN::randomExpr).limit(1000);
    }

    @ParameterizedTest
    @MethodSource("randomExpression")
    public void parsedIsSameAsFormatted(Expr expr) {
        assertEquals(expr, new Parser(expr.format()).parseExpression());
    }

    public static Stream<String> randomExpressionString() {
        return randomExpression().map(Expr::format);
    }

    @ParameterizedTest
    @MethodSource("randomExpressionString")
    public void formattedIsSameAsParsed(String string) {
        assertEquals(string, new Parser(string).parseExpression().format());
    }

    public static IntStream smallSize() {
        return IntStream.range(0, 100);
    }

    @ParameterizedTest
    @MethodSource("smallSize")
    public void generatedHasCorrectSize(int size) {
        IntStream.range(0, 10).forEach(unused -> {
            final Expr generated = GEN.randomExpr(size);
            final long genSize = generated.included()
                    .filter(e -> !(e instanceof Expr.Const))
                    .count();
            assertEquals(size, genSize);
        });
    }
}
