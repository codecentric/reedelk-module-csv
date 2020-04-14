package com.reedelk.csv.internal;

import com.reedelk.runtime.api.commons.SerializableUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class CSVMetadata extends HashMap<String, Serializable> {

    private static final String ATTRIBUTE_COLUMN_NAMES = "columnNames";

    public CSVMetadata(List<String> columnNames) {
        put(ATTRIBUTE_COLUMN_NAMES, SerializableUtils.asSerializableList(columnNames));
    }

    public CSVMetadata() {
    }

    public boolean hasColumnNames() {
        return containsKey(ATTRIBUTE_COLUMN_NAMES);
    }

    @SuppressWarnings("unchecked")
    public List<String> columnNames() {
        return (List<String>) get(ATTRIBUTE_COLUMN_NAMES);
    }
}
