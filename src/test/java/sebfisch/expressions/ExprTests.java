package sebfisch.expressions;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import sebfisch.expressions.data.Expr;

class ExprTests {

    public static Stream<Expr> randomExpression() {
        return Stream.generate(new Generator()::randomExpression).limit(1000);
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
}
