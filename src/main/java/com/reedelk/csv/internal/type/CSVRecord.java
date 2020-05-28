package com.reedelk.csv.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.*;

@Type(displayName = "CSVRecord", mapKeyType = String.class, mapValueType = String.class)
public class CSVRecord extends HashMap<String, String> {

    private final List<String> values;
    private final Map<String,Integer> headerNameIndexMap;

    public CSVRecord(Map<String,Integer> headerNameIndexMap, List<String> values) {
        this.values = values;
        this.headerNameIndexMap = headerNameIndexMap;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return headerNameIndexMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    @Override
    public String get(Object key) {
        Integer valueIndex = headerNameIndexMap.get(key);
        return values.get(valueIndex);
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return headerNameIndexMap.keySet();
    }

    @Override
    public Collection<String> values() {
        return values;
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String,Integer> entry : headerNameIndexMap.entrySet()) {
            String columnName = entry.getKey();
            Integer columnIndex = entry.getValue();
            map.put(columnName, values.get(columnIndex));
        }
        return map.entrySet();
    }
}
