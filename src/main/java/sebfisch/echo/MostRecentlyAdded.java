package sebfisch.echo;

import java.util.SequencedCollection;
import java.util.concurrent.ConcurrentLinkedDeque;

public record MostRecentlyAdded<T>(int capacity, SequencedCollection<T> elements) {

    public MostRecentlyAdded(int capacity) {
        this(capacity, new ConcurrentLinkedDeque<>());
    }

    public void add(T elem) {
        elements.addLast(elem);
        while (capacity < elements.size()) {
            elements.removeFirst();
        }
    }
}
