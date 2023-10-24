package sebfisch.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ExprTests {

    @Test
    void testThatSimplifyRemovesLeftZero() {
        assertEquals("10", Simplification.TRANSFORM.apply(
                new Parser("(0 + 10)").parseExpression()
        ).format());
    }

    @Test
    void testThatSimplifyRemovesRightZero() {
        assertEquals("11", Simplification.TRANSFORM.apply(
                new Parser("(11 + 0)").parseExpression()
        ).format());
    }

    @Test
    void testThatSimplifyRemovesTwoZeroes() {
        assertEquals("12", Simplification.TRANSFORM.apply(
                new Parser("(0 + (12 + 0))").parseExpression()
        ).format());
    }
}
