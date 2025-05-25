package huy.project.profile_service.core.port;

import java.util.function.Function;

public interface IMultiLevelCache<K, V> {
    V get(K key, Function<K, V> supplier);
    void put(K key, V value);
    void invalidate(K key);
}
