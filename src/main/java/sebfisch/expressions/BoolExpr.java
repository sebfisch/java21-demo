package sebfisch.expressions;

import sebfisch.util.Tree;

public sealed interface BoolExpr extends Tree<BoolExpr> {

    public enum Const implements BoolExpr {
        TRUE, FALSE
    }

    public record Not(BoolExpr child) implements BoolExpr, Tree.Unary<BoolExpr> {

        @Override
        public BoolExpr withChild(BoolExpr child) {
            return new Not(child);
        }
    }

    public sealed interface Bin extends BoolExpr, Tree.Binary<BoolExpr> permits And, Or {
    }

    public record And(BoolExpr left, BoolExpr right) implements Bin {

        @Override
        public BoolExpr withChildren(BoolExpr left, BoolExpr right) {
            return new And(left, right);
        }
    }

    public record Or(BoolExpr left, BoolExpr right) implements Bin {

        @Override
        public BoolExpr withChildren(BoolExpr left, BoolExpr right) {
            return new Or(left, right);
        }
    }

    default boolean hasOr() {
        return included().filter(e -> e instanceof Or).count() > 0;
    }

    default boolean hasDoubleNot() {
        return included().filter(e -> e instanceof Not(Not(var unused))).count() > 0;
    }
}
