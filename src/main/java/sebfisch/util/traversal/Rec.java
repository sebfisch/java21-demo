package sebfisch.util.traversal;

public interface Rec<C extends Rec<C>> extends Has<C, C> {

    public interface One<C extends Rec<C>> extends Has.One<C, C> {
    }

    public interface Two<C extends Rec<C>> extends Has.Two<C, C> {
    }

    public interface Any<C extends Rec<C>> extends Has.Any<C, C> {
    }
}
