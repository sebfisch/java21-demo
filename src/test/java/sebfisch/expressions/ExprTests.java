package sebfisch.expressions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static sebfisch.expressions.Parser.EXPR;
import sebfisch.util.traversal.Traverse;

class ExprTests {

    @Test
    void testEvaluatingSimpleExpression() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        assertEquals(3, expr.value());
    }

    @Test
    void testDivisionByZero() {
        final Expr expr = new Expr.Div(Expr.Small.ONE, Expr.Small.ZERO);
        assertThrows(ArithmeticException.class, () -> expr.value());
    }

    @Test
    void testIntOverflow() {
        final Expr expr = new Expr.Add(new Expr.Num(Integer.MAX_VALUE), Expr.Small.ONE);
        assertEquals(Integer.MIN_VALUE, expr.value());
    }

    @Test
    void testDivOverflow() {
        final Expr expr = new Expr.Div(
                new Expr.Num(Integer.MIN_VALUE),
                new Expr.Neg(Expr.Small.ONE)
        );
        assertEquals(Integer.MIN_VALUE, expr.value());
    }

    @Test
    void testFormattingSimpleExpression() {
        final Expr expr = new Expr.Add(Expr.Small.ONE, new Expr.Num(2));
        final String string = "(1 + 2)";
        assertEquals(string, expr.format());
    }

    @Test
    void testFormattingParsedExpr() {
        final String string = "(1 + 2)";
        assertEquals(string, new Parser(string).parseExpression().format());
    }

    @Test
    void testParsingFormattedExpr() {
        final Expr expr = EXPR
        ."(1 + 2)";
        assertEquals(expr, new Parser(expr.format()).parseExpression());
    }

    @Test
    void testTraversingChildren() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.children(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(2, counter.intValue());
    }

    @Test
    void testTraversingNested() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.nested(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(3, counter.intValue());
    }

    @Test
    void testTraversingAll() {
        final Expr expr = new Expr.Add(new Expr.Neg(Expr.Small.ONE), new Expr.Num(2));
        AtomicInteger counter = new AtomicInteger(0);
        Traverse.all(expr, e -> {
            counter.incrementAndGet();
        });
        assertEquals(4, counter.intValue());
    }

    @Test
    void testSimplifyingSimpleExpression() {
        final Expr expr = new Expr.Mul(new Expr.Num(0), Expr.Small.ONE);
        assertEquals(Expr.Small.ZERO, Simpler.expression(expr));
    }

    private static final Generator GEN = new Generator();

    static Stream<Expr> randomExpression() {
        return Stream.generate(GEN::randomExpr).limit(1000);
    }

    @ParameterizedTest
    @MethodSource("randomExpression")
    void parsedIsSameAsFormatted(Expr expr) {
        assertEquals(expr, new Parser(expr.format()).parseExpression());
    }

    static Stream<String> randomExpressionString() {
        return randomExpression().map(Expr::format);
    }

    @ParameterizedTest
    @MethodSource("randomExpressionString")
    void formattedIsSameAsParsed(String string) {
        assertEquals(string, new Parser(string).parseExpression().format());
    }

    private record ExprWithSize(Expr expr, int size) {

    }

    static Stream<ExprWithSize> smallRandomExpression() {
        return IntStream.range(0, 100).boxed().mapMulti((size, addToStream) -> {
            IntStream.range(0, 10).forEach(unused -> {
                addToStream.accept(new ExprWithSize(GEN.randomExpr(size), size));
            });
        });
    }

    @ParameterizedTest
    @MethodSource("smallRandomExpression")
    void generatedHasCorrectSize(ExprWithSize e) {
        assertEquals(e.size(), e.expr().opCount());
    }
}
