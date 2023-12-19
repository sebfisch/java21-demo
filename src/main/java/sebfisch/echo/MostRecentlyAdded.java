package sebfisch.echo;

import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.concurrent.ConcurrentLinkedDeque;

public record MostRecentlyAdded<T>(int capacity, SequencedCollection<T> elements) {

    public MostRecentlyAdded(int capacity) {
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
