package org.bitcoinpotato.util;

import com.google.common.base.Function;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;

public class Maps3 {
    public static <K, V> Map<K, V> build(Iterable<K> keys, Function<K, V> buildValue) {
        Map<K, V> result = newHashMap();
        for (K key : keys) {
            checkArgument(!result.containsKey(key));
            V value = buildValue.apply(key);
            result.put(key, value);
        }
        return result;
    }

    public static <K, V1, V2> Map<K, V2> build(Map<K, V1> input, Func2<K, V1, V2> buildValue) {
        Map<K, V2> result = newHashMap();
        for (Map.Entry<K, V1> entry : input.entrySet()) {
            checkArgument(!result.containsKey(entry.getKey()));
            K key = entry.getKey();
            V1 value1 = entry.getValue();
            V2 value = buildValue.apply(key, value1);
            result.put(key, value);
        }
        return result;
    }
}
