package sebfisch.util.traversal;

public interface Rec<C extends Rec<C>> extends Has<C, C> {

    interface Unary<C extends Rec<C>> extends Has.One<C, C> {
    }

    interface Binary<C extends Rec<C>> extends Has.Two<C, C> {
    }

    interface Arbitrary<C extends Rec<C>> extends Has.Any<C, C> {
    }
}
