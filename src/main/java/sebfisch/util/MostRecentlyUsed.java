package sebfisch.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public record MostRecentlyUsed<K, V>(
        int capacity, RecentlyAccessed<K> keys, ConcurrentHashMap<K, V> entries) {

    public MostRecentlyUsed(int capacity) {
        this(capacity, new RecentlyAccessed<>(capacity), new ConcurrentHashMap<>(capacity, 1));
    }

    public V computeIfAbsent(K key, Function<K, V> function) {
        // do not use entries.computeIfAbsent, to avoid recursive update
        V result = entries.get(key);
        if (result == null) {
            result = function.apply(key);
            entries.put(key, result);
        }
        if (!keys.elements().contains(key)) {
            keys.add(key).forEach(entries::remove);
        }
        return result;
    }
}
