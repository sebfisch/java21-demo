package sebfisch.expressions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExprTests {

    @Test
    public void testFormattingSimpleExpression() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        final String string = "(1 + 2)";
        assertEquals(string, expr.format());
    }

    @Test
    public void testFormattingParsedExpr() {
        final String string = "(1 + 2)";
        assertEquals(string, new Parser(string).parseExpression().format());
    }

    @Test
    public void testParsingFormattedExpr() {
        final Expr expr = new Parser("(1 + 2)").parseExpression();
        assertEquals(expr, new Parser(expr.format()).parseExpression());
    }

    @Test
    public void testTraversingSimpleExpression() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        expr.forEachIncluded(e -> {
            counter.incrementAndGet();
        });
        assertEquals(3, counter.intValue());
    }

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

    private record ExprWithSize(Expr expr, int size) {

    }

    public static Stream<ExprWithSize> smallRandomExpression() {
        return IntStream.range(0, 100).boxed().mapMulti((size, addToStream) -> {
            IntStream.range(0, 10).forEach(unused -> {
                addToStream.accept(new ExprWithSize(GEN.randomExpr(size), size));
            });
        });
    }

    @ParameterizedTest
    @MethodSource("smallRandomExpression")
    public void generatedHasCorrectSize(ExprWithSize e) {
        assertEquals(e.size(), e.expr().size());
    }
}
