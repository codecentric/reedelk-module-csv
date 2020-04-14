package com.reedelk.csv.internal.write;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.message.content.ListContent;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.List;

import static com.reedelk.csv.internal.commons.Preconditions.checkSuitableTypeOrThrow;

public class CSVWriter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(Message message, CSVPrinter csvPrinter, boolean includeHeaders, List<String> headers) throws IOException {
        // We must have a list of lists
        TypedContent<?,?> content = message.content();

        if (includeHeaders) {
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
                // If include headers, we write the first X items from the list.
                csvPrinter.printRecord((List<Object>) object);

            } else if (object instanceof DataRow) {
                // IF headers, we get the headers, if not found then nothing
                DataRow row = (DataRow) object;
                csvPrinter.printRecord(row.values());

            } else {
                csvPrinter.printRecord(object);
            }
        }

        csvPrinter.flush();
    }
}
