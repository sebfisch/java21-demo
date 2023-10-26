package sebfisch.util;

import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.concurrent.ConcurrentLinkedDeque;

public record RecentlyAccessed<T>(int capacity, SequencedCollection<T> elements) {

    public RecentlyAccessed(int capacity) {
        this(capacity, new ConcurrentLinkedDeque<>());
    }

    public SequencedCollection<T> add(T elem) {
        elements.addLast(elem);
        SequencedCollection<T> removed = new LinkedList<>();
        while (capacity < elements.size()) {
            removed.addLast(elements.removeFirst());
        }
        return removed;
    }
}
