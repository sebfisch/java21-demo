package sebfisch.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public record MostRecentlyUsed<K, V>(
        int capacity, RecentlyAccessed<K> keys, ConcurrentHashMap<K, V> entries) {

    public MostRecentlyUsed(int capacity) {
        this(capacity, new RecentlyAccessed<>(capacity), new ConcurrentHashMap<>(capacity, 1));
    }

    public V computeIfAbsent(K key, Function<K, V> function) {
        final V result = entries.computeIfAbsent(key, function);
        if (!keys.elements().contains(key)) {
            keys.add(key).forEach(entries::remove);
        }
        return result;
    }
}
