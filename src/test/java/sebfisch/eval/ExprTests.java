package sebfisch.eval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ExprTests {

    @Test
    void testThatSimplifyRemovesLeftZero() {
        assertEquals("1", Util.simplify(
                new Expr.Add(Expr.Small.ZERO, Expr.Small.ONE)
        ).format());
    }

    @Test
    void testThatSimplifyRemovesRightZero() {
        assertEquals("1", Util.simplify(
                new Expr.Add(Expr.Small.ONE, new Expr.Num(0))
        ).format());
    }

    @Test
    void testThatSimplifyRemovesTwoZeroes() {
        assertEquals("1", Util.simplify(
                new Expr.Add(
                        new Expr.Num(0),
                        new Expr.Add(Expr.Small.ONE, Expr.Small.ZERO)
                )
        ).format());
    }
}
