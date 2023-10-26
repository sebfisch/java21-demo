package sebfisch.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public record MostRecentlyUsed<K, V>(int capacity, HashMap<K, V> entries) {

    public MostRecentlyUsed(int capacity) {
        this(capacity, new LinkedHashMap<>(capacity + 1, 1, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
                return size() > capacity;
            }
        });
    }

    public synchronized V computeIfAbsent(K key, Function<K, V> function) {
        return entries.computeIfAbsent(key, function);
    }
}
