package com.reedelk.csv.internal;

import com.reedelk.runtime.api.message.content.DataRow;

import java.util.List;

public class CSVDataRow implements DataRow<String> {

    private final List<String> headerNames;
    private final List<String> values;

    public CSVDataRow(List<String> headerNames, List<String> values) {
        this.headerNames = headerNames;
        this.values = values;
    }

    public CSVDataRow(List<String> values) {
        this.headerNames = null;
        this.values = values;
    }

    @Override
    public int columnCount() {
        return values.size();
    }

    @Override
    public String columnName(int i) {
        if (headerNames != null) {
            return headerNames.get(i);
        } else {
            throw new IllegalArgumentException("Header names not available");
        }
    }

    @Override
    public List<String> columnNames() {
        if (headerNames != null) {
            return headerNames;
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
        if (headerNames == null) {
            throw new IllegalArgumentException("Header names not available");
        }
        int index = -1;
        for (int i = 0; i < headerNames.size(); i++) {
            if (headerNames.get(i).equals(columnName)) {
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
    public List<String> row() {
        return values;
    }

    @Override
    public String toString() {
        if (headerNames != null) {
            return "CSVDataRow{" +
                    "headerNames=" + headerNames +
                    ", values=" + values +
                    '}';
        } else {
            return "CSVDataRow{" +
                    ", values=" + values +
                    '}';
        }
    }
}
