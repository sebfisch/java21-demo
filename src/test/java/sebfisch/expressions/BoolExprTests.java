package sebfisch.expressions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

class BoolExprTests {

    @Test
    void testNormalization() {
        final BoolExpr be = new BoolExpr.Not(new BoolExpr.Or(
                new BoolExpr.Not(BoolExpr.Const.TRUE),
                new BoolExpr.Not(BoolExpr.Const.FALSE)
        ));
        final BoolExpr result = Simpler.boolExpr(be);
        assertFalse(result.hasOr());
        assertFalse(result.hasDoubleNot());
    }
}
