package sebfisch.expressions;

import sebfisch.util.traversal.Query;
import sebfisch.util.traversal.Rec;

public sealed interface BoolExpr extends Rec<BoolExpr> {

    enum Const implements BoolExpr {
        TRUE, FALSE
    }

    record Not(BoolExpr child) implements BoolExpr, Rec.Unary<BoolExpr> {

        @Override
        public Not withChild(BoolExpr child) {
            return new Not(child);
        }
    }

    sealed interface Bin extends BoolExpr, Rec.Binary<BoolExpr> permits And, Or {
    }

    record And(BoolExpr left, BoolExpr right) implements Bin {

        @Override
        public And withChildren(BoolExpr left, BoolExpr right) {
            return new And(left, right);
        }
    }

    record Or(BoolExpr left, BoolExpr right) implements Bin {

        @Override
        public Or withChildren(BoolExpr left, BoolExpr right) {
            return new Or(left, right);
        }
    }

    default boolean hasDoubleNot() {
        return Query.all(this).filter(e -> e instanceof Not(Not(var unused))).count() > 0;
    }

    default boolean hasOr() {
        return Query.all(this).filter(e -> e instanceof Or).count() > 0;
    }
}
