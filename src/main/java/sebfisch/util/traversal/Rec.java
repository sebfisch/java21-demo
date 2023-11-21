package sebfisch.util.traversal;

public interface Rec<C extends Rec<C>> extends Has<C, C> {

    public interface Unary<C extends Rec<C>> extends Has.One<C, C> {
    }

    public interface Binary<C extends Rec<C>> extends Has.Two<C, C> {
    }

    public interface Arbitrary<C extends Rec<C>> extends Has.Any<C, C> {
    }
}
