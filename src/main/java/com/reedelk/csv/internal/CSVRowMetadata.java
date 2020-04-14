package com.reedelk.csv.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVRowMetadata extends HashMap<String, Serializable> {

    private static final String ATTRIBUTE_COLUMN_NAMES = "columnNames";

    public CSVRowMetadata(List<String> columnNames) {
        ArrayList<String> serializableList = new ArrayList<>(columnNames);
        put(ATTRIBUTE_COLUMN_NAMES, serializableList);
    }

    public CSVRowMetadata() {
    }

    public boolean hasColumnNames() {
        return containsKey(ATTRIBUTE_COLUMN_NAMES);
    }

    public List<String> columnNames() {
        return (List<String>) get(ATTRIBUTE_COLUMN_NAMES);
    }
}
