package sebfisch.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import sebfisch.expressions.data.*;

class ExprTests {

    @Test
    void testThatSimplifyRemovesLeftZero() {
        assertEquals("1", new Simplification().apply(
                new Add(Small.ZERO, Small.ONE)
        ).format());
    }

    @Test
    void testThatSimplifyRemovesRightZero() {
        assertEquals("1", new Simplification().apply(
                new Add(Small.ONE, new Num(0))
        ).format());
    }

    @Test
    void testThatSimplifyRemovesTwoZeroes() {
        assertEquals("1", new Simplification().apply(
                new Add(
                        new Num(0),
                        new Add(Small.ONE, Small.ZERO)
                )
        ).format());
    }
}
