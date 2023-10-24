package sebfisch.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static sebfisch.expressions.Parser.EXPR;

class ExprTests {

    @Test
    void testThatSimplifyRemovesLeftZero() {
        assertEquals("10", Simplification.TRANSFORM.apply(
                EXPR."(0 + 10)"
        ).format());
    }

    @Test
    void testThatSimplifyRemovesRightZero() {
        assertEquals("11", Simplification.TRANSFORM.apply(
                EXPR."(11 + 0)"
        ).format());
    }

    @Test
    void testThatSimplifyRemovesTwoZeroes() {
        assertEquals("12", Simplification.TRANSFORM.apply(
                EXPR."(0 + (12 + 0))"
        ).format());
    }

    @Test
    void testThatSimplifyRemovesDoubleNegWithTemplate() {
        int neg = -13;
        assertEquals("13", Simplification.TRANSFORM.apply(
                EXPR."-\{neg}"
        ).format());
    }
}
