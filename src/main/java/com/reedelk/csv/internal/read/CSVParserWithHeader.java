package com.reedelk.csv.internal.read;

import com.reedelk.csv.internal.exception.CSVReadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// with header
public class CSVParserWithHeader {

    private CSVParserWithHeader() {
    }

    public static List<com.reedelk.csv.internal.type.CSVRecord> from(CSVFormat csvFormat, Reader input) {
        try {
            org.apache.commons.csv.CSVParser parse = csvFormat.parse(input);
            List<CSVRecord> records = parse.getRecords();
            List<String> headerNames = parse.getHeaderNames();
            return CSVParserWithHeader.convert(records, headerNames);
        } catch (IOException exception) {
            throw new CSVReadException(exception.getMessage(), exception);
        }
    }

    private static List<com.reedelk.csv.internal.type.CSVRecord> convert(List<CSVRecord> records, List<String> headerNames) {

        List<com.reedelk.csv.internal.type.CSVRecord> mapped = new ArrayList<>();

        Map<String,Integer> headerNameIndexMap = new HashMap<>();
        for (int i = 0; i < headerNames.size(); i++) {
            headerNameIndexMap.put(headerNames.get(i), i);
        }

        // Maybe start from 1?
        for (CSVRecord record : records) {
            List<String> rowData = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                rowData.add(record.get(i));
            }
            mapped.add(new com.reedelk.csv.internal.type.CSVRecord(headerNameIndexMap, rowData));
        }

        return mapped;
    }
}
