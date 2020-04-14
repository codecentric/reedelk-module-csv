package com.reedelk.csv.internal.read;

import com.reedelk.csv.internal.CSVDataRow;
import com.reedelk.csv.internal.CSVMetadata;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.message.content.DataRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CSVParser {

    @SuppressWarnings("rawtypes")
    public static List<DataRow> from(CSVFormat csvFormat, Reader input, Boolean firstRecordAsHeader) {
        try {
            org.apache.commons.csv.CSVParser parse = csvFormat.parse(input);
            List<CSVRecord> records = parse.getRecords();
            List<String> headerNames = parse.getHeaderNames();

            return CSVParser.convert(records, headerNames, firstRecordAsHeader);
        } catch (IOException exception) {
            throw new PlatformException(exception.getMessage(), exception);
        }
    }

    @SuppressWarnings("rawtypes")
    public static List<DataRow> convert(List<CSVRecord> records,
                                        List<String> headerNames,
                                        Boolean firstRecordAsHeader) {

        boolean realFirstRecordAsHeader = Optional.ofNullable(firstRecordAsHeader).orElse(false);
        List<DataRow> mapped = new ArrayList<>();

        CSVMetadata metadata = null;
        for (CSVRecord record : records) {
            List<String> rowData = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                rowData.add(record.get(i));
            }

            if (metadata == null) {
                metadata = realFirstRecordAsHeader ?
                        new CSVMetadata(headerNames) : new CSVMetadata();
            }

            mapped.add(new CSVDataRow(metadata, rowData));
        }

        return mapped;
    }
}
