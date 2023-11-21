package sebfisch.util.traversal;

import java.util.List;

public interface Has<P extends Has<P, C>, C> {

    interface One<P extends Has<P, C>, C> extends Has<P, C> {

        C child();

        P withChild(C child);
    }

    interface Two<P extends Has<P, C>, C> extends Has<P, C> {

        C left();

        C right();

        P withChildren(C left, C right);
    }

    interface Any<P extends Has<P, C>, C> extends Has<P, C> {

        List<C> children();

        P withChildren(List<C> children);
    }
}
