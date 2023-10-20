package sebfisch.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import sebfisch.expressions.data.Add;
import sebfisch.expressions.data.Num;
import sebfisch.expressions.data.Small;

class ExprTests {

    @Test
    void testThatSimplifyRemovesLeftZero() {
        assertEquals("1", Simplification.TRANSFORM.apply(
                new Add(Small.ZERO, Small.ONE)
        ).format());
    }

    @Test
    void testThatSimplifyRemovesRightZero() {
        assertEquals("1", Simplification.TRANSFORM.apply(
                new Add(Small.ONE, new Num(0))
        ).format());
    }

    @Test
    void testThatSimplifyRemovesTwoZeroes() {
        assertEquals("1", Simplification.TRANSFORM.apply(
                new Add(
                        new Num(0),
                        new Add(Small.ONE, Small.ZERO)
                )
        ).format());
    }
}
