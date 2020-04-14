package com.reedelk.csv.internal;

import com.reedelk.runtime.api.message.content.DataRow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CSVDataRow implements DataRow<String> {

    private final CSVRowMetadata attributes;
    private final List<String> values;

    public CSVDataRow(CSVRowMetadata attributes, List<String> values) {
        this.attributes = attributes;
        this.values = values;
    }

    @Override
    public Map<String, Serializable> attributes() {
        return attributes;
    }

    @Override
    public Serializable attribute(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public int columnCount() {
        return values.size();
    }

    @Override
    public String columnName(int i) {
        if (attributes.hasColumnNames()) {
            return attributes.columnNames().get(i);
        } else {
            throw new IllegalArgumentException("Header names not available");
        }
    }

    @Override
    public List<String> columnNames() {
        if (attributes.hasColumnNames()) {
            return attributes.columnNames();
        } else {
            throw new IllegalArgumentException("Header names not available");
        }
    }

    @Override
    public String get(int i) {
        return values.get(i);
    }

    @Override
    public String getByColumnName(String columnName) {
        if (!attributes.hasColumnNames()) {
            throw new IllegalArgumentException("Header names not available");
        }
        int index = -1;
        for (int i = 0; i < attributes.columnNames().size(); i++) {
            if (attributes.columnNames().get(i).equals(columnName)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("There are no headers with name " + columnName);
        }
        return values.get(index);
    }

    @Override
    public List<String> getColumnNames() {
        return attributes.columnNames();
    }

    @Override
    public List<String> values() {
        return values;
    }

    @Override
    public String toString() {
        // Column names might be null when the CSV file
        // does not start with column names.
        if (attributes.hasColumnNames()) {
            return "CSVDataRow{" +
                    "columnNames=" + attributes.columnNames() +
                    ", values=" + values +
                    '}';
        } else {
            return "CSVDataRow{" +
                    "values=" + values +
                    '}';
        }
    }
}
