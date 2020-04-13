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

    @Override
    public int columnCount() {
        return headerNames.size();
    }

    @Override
    public String columnName(int i) {
        return headerNames.get(i);
    }

    @Override
    public List<String> columnNames() {
        return headerNames;
    }

    @Override
    public String get(int i) {
        return values.get(i);
    }

    @Override
    public String getByColumnName(String columnName) {
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
        return "CSVDataRow{" +
                "headerNames=" + headerNames +
                ", values=" + values +
                '}';
    }
}
