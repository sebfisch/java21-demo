package sebfisch.util.traversal;

import java.util.List;

public interface Has<P extends Has<P, C>, C> {

    public interface One<P extends Has<P, C>, C> extends Has<P, C> {

        C child();

        P withChild(C child);
    }

    public interface Two<P extends Has<P, C>, C> extends Has<P, C> {

        C left();

        C right();

        P withChildren(C left, C right);
    }

    public interface Any<P extends Has<P, C>, C> extends Has<P, C> {

        List<C> children();

        P withChildren(List<C> children);
    }
}
