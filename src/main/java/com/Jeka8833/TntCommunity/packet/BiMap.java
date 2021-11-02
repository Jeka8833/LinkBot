package com.Jeka8833.TntCommunity.packet;

import java.util.HashMap;
import java.util.Map;

public class BiMap<K, V> {

    private final Map<K, V> keyToValueMap = new HashMap<>();
    private final Map<V, K> valueToKeyMap = new HashMap<>();

    synchronized public void put(K key, V value) {
        keyToValueMap.put(key, value);
        valueToKeyMap.put(value, key);
    }

    synchronized public V removeByKey(K key) {
        V removedValue = keyToValueMap.remove(key);
        valueToKeyMap.remove(removedValue);
        return removedValue;
    }

    synchronized public K removeByValue(V value) {
        K removedKey = valueToKeyMap.remove(value);
        keyToValueMap.remove(removedKey);
        return removedKey;
    }

    public boolean containsKey(K key) {
        return keyToValueMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        return keyToValueMap.containsValue(value);
    }

    public K getKey(V value) {
        return valueToKeyMap.get(value);
    }

    public V get(K key) {
        return keyToValueMap.get(key);
    }

}
