package sebfisch.util;

import java.util.SequencedCollection;
import java.util.concurrent.ConcurrentLinkedDeque;

public record RecentlyAccessed<T>(int capacity, SequencedCollection<T> elements) {

    public RecentlyAccessed(int capacity) {
        this(capacity, new ConcurrentLinkedDeque());
    }

    public void add(T elem) {
        elements.addLast(elem);
        if (capacity < elements.size()) {
            elements.removeFirst();
        }
    }
}
