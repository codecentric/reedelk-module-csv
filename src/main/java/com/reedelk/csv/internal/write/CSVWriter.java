package com.reedelk.csv.internal.write;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.message.content.ListContent;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.reedelk.csv.internal.commons.Preconditions.checkSuitableTypeOrThrow;

public class CSVWriter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(Message message, CSVPrinter csvPrinter, boolean includeHeaders, List<String> headers) throws IOException {
        // We must have a list of lists
        TypedContent<?,?> content = message.content();

        if (includeHeaders) {
            // If there are headers, we must write them first.
            csvPrinter.printRecord(headers);
        }

        // If the payload is null we write empty.
        if (content.data() == null) {
            csvPrinter.flush();
            return;
        }

        checkSuitableTypeOrThrow(content);

        ListContent<Object> list = (ListContent<Object>) content;

        for (Object object : list.data()) {

            if (object == null) continue;

            if (object instanceof List) {
                csvPrinter.printRecord((List<Object>) object);

            } else if (object instanceof DataRow) {
                // If the object has type DataRow, we might want to write only a few headers,
                // therefore for each element in the data row we find the matching column index
                // of the wanted header name of the value and then add it to the list of items
                // to print in the record.
                DataRow row = (DataRow) object;
                if (includeHeaders) {
                    Serializable[] printRow = new Serializable[headers.size()];
                    for (int i = 0; i < headers.size(); i++) {
                        String headerName = headers.get(i);
                        Serializable valueForColumn = row.getByColumnName(headerName);
                        printRow[i] = valueForColumn;
                    }
                    csvPrinter.printRecord(printRow);
                } else {
                    csvPrinter.printRecord(row.values());
                }

            } else {
                csvPrinter.printRecord(object);
            }
        }

        csvPrinter.flush();
    }
}
