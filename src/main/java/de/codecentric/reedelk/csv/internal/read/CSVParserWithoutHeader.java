package de.codecentric.reedelk.csv.internal.read;

import de.codecentric.reedelk.csv.internal.exception.CSVReadException;
import de.codecentric.reedelk.runtime.api.type.ListOfString;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

// no header
public class CSVParserWithoutHeader {

    private CSVParserWithoutHeader() {
    }

    public static List<ListOfString> from(CSVFormat csvFormat, Reader input) {
        try {
            org.apache.commons.csv.CSVParser parse = csvFormat.parse(input);
            List<CSVRecord> records = parse.getRecords();
            return CSVParserWithoutHeader.convert(records);
        } catch (IOException exception) {
            throw new CSVReadException(exception.getMessage(), exception);
        }
    }

    private static List<ListOfString> convert(List<CSVRecord> records) {
        List<ListOfString> allRecords = new ArrayList<>();
        for (CSVRecord record : records) {
            ListOfString rowData = new ListOfString();
            for (int i = 0; i < record.size(); i++) {
                rowData.add(record.get(i));
            }
            allRecords.add(rowData);
        }
        return allRecords;
    }
}
