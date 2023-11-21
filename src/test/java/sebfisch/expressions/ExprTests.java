package sebfisch.expressions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import sebfisch.util.Partial;
import sebfisch.util.traversal.Traverse;

class ExprTests {

    @Test
    public void testEvaluatingSimpleExpression() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        assertEquals(3, expr.value());
    }

    @Test
    public void testDivisionByZero() {
        final Expr expr = new Expr.Div(Expr.Small.ONE, Expr.Small.ZERO);
        assertThrows(ArithmeticException.class, () -> expr.value());
    }

    @Test
    public void testIntOverflow() {
        final Expr expr = new Expr.Add(new Expr.Num(Integer.MAX_VALUE), Expr.Small.ONE);
        assertThrows(ArithmeticException.class, () -> expr.value());
    }

    @Test
    public void testDivOverflow() {
        final Expr expr = new Expr.Div(
                new Expr.Num(Integer.MIN_VALUE),
                new Expr.Neg(Expr.Small.ONE)
        );
        assertThrows(ArithmeticException.class, () -> expr.value());
    }

    @Test
    public void testPartialDivisionByZero() {
        final Expr expr = new Expr.Div(Expr.Small.ONE, Expr.Small.ZERO);
        assertTrue(
                expr.partialValue() instanceof Partial.Failure(var e)
                && e instanceof ArithmeticException
        );
    }

    @Test
    public void testPartialIntOverflow() {
        final Expr expr = new Expr.Add(new Expr.Num(Integer.MAX_VALUE), Expr.Small.ONE);
        assertTrue(
                expr.partialValue() instanceof Partial.Failure(var e)
                && e instanceof ArithmeticException
        );
    }

    @Test
    public void testPartialDivOverflow() {
        final Expr expr = new Expr.Div(
                new Expr.Num(Integer.MIN_VALUE),
                new Expr.Neg(Expr.Small.ONE)
        );
        assertTrue(
                expr.partialValue() instanceof Partial.Failure(var e)
                && e instanceof ArithmeticException
        );
    }

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
    public void testTraversingChildren() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.children(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(2, counter.intValue());
    }

    @Test
    public void testTraversingNested() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.nested(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(3, counter.intValue());
    }

    @Test
    public void testTraversingAll() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.all(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(4, counter.intValue());
    }

    @Test
    public void testTraversingConstants() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        int[] constants = expr.includedConstants().toArray();
        assertArrayEquals(new int[]{1, 2}, constants);
    }

    @Test
    public void testSimplifyingSimpleExpression() {
        final Expr expr = new Expr.Mul(new Expr.Num(0), Expr.Small.ONE);
        assertEquals(Expr.Small.ZERO, Simpler.expression(expr));
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

    public static Stream<Expr> safeRandomExpression() {
        return Stream.generate(GEN::randomExpr)
                .filter(e -> e.partialValue() instanceof Partial.Success)
                .limit(1000);
    }

    @ParameterizedTest
    @MethodSource("safeRandomExpression")
    public void simplifiedHasSameResult(Expr expr) {
        assertEquals(expr.value(), Simpler.expression(expr).value());
    }
}
