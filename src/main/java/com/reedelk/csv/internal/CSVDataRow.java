package com.reedelk.csv.internal;

import com.reedelk.runtime.api.message.content.DataRow;

import java.util.List;

public class CSVDataRow implements DataRow<String> {

    private final List<String> columnNames;
    private final List<String> values;

    public CSVDataRow(List<String> columnNames, List<String> values) {
        this.columnNames = columnNames;
        this.values = values;
    }

    public CSVDataRow(List<String> values) {
        this.columnNames = null;
        this.values = values;
    }

    @Override
    public int columnCount() {
        return values.size();
    }

    @Override
    public String columnName(int i) {
        if (columnNames != null) {
            return columnNames.get(i);
        } else {
            throw new IllegalArgumentException("Header names not available");
        }
    }

    @Override
    public List<String> columnNames() {
        if (columnNames != null) {
            return columnNames;
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
        if (columnNames == null) {
            throw new IllegalArgumentException("Header names not available");
        }
        int index = -1;
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(columnName)) {
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
        return columnNames;
    }

    @Override
    public List<String> values() {
        return values;
    }

    @Override
    public String toString() {
        // Column names might be null when the CSV file
        // does not start with column names.
        if (columnNames != null) {
            return "CSVDataRow{" +
                    "columnNames=" + columnNames +
                    ", values=" + values +
                    '}';
        } else {
            return "CSVDataRow{" +
                    "values=" + values +
                    '}';
        }
    }
}
