package sebfisch.echo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.SequencedCollection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public record MostRecentlyAdded<T>(int capacity, SequencedCollection<T> elements, Lock lock) {

    public MostRecentlyAdded(int capacity) {
        this(capacity, new LinkedHashSet<>(), new ReentrantLock());
    }

    public SequencedCollection<T> add(T elem) {
        lock.lock();
        try {
            elements.addLast(elem);
            SequencedCollection<T> removed = new LinkedList<>();
            while (capacity < elements.size()) {
                removed.addLast(elements.removeFirst());
            }
            return removed;
        } finally {
            lock.unlock();
        }
    }

    public Collection<T> copy() {
        lock.lock();
        try {
            return new ArrayList<>(elements);
        } finally {
            lock.unlock();
        }
    }
}
