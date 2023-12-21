package sebfisch.util.traversal;

import java.util.function.Consumer;

public final class Traverse {

    public static <P extends Has<P, C>, C> void children(P parent, Consumer<C> action) {
        switch (parent) {
            case Has.One/*<P,C>*/ p -> // parameterized type pattern handled incorrectly by Eclipse
                action.accept((C) p.child()); // type cast required as a consequence
            case Has.Two/*<P,C>*/ p -> {
                action.accept((C) p.left());
                action.accept((C) p.right());
            }
            case Has.Any/*<P,C>*/ p ->
                p.children().forEach(action);
            default -> {
            }
        }
    }

    public static <P extends Has<P, C>, C extends Has<C, C>> void nested(P parent, Consumer<C> action) {
        Traverse.children(parent, child -> Traverse.all(child, action));
    }

    public static <C extends Has<C, C>> void all(C parent, Consumer<C> action) {
        action.accept(parent);
        Traverse.nested(parent, action);
    }

    private Traverse() {
    }
}
