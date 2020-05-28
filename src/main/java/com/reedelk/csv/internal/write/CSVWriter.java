package com.reedelk.csv.internal.write;

import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.ListContent;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.reedelk.csv.internal.commons.Preconditions.checkSuitableTypeOrThrow;

public class CSVWriter {

    private CSVWriter() {
    }

    @SuppressWarnings({"unchecked"})
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

            if (object instanceof List) {
                csvPrinter.printRecord((List<Object>) object);
            } else if (object instanceof Map) {
                Map<?,?> map = (Map<?,?>) object;
                if (includeHeaders) {
                    List<Object> recordWithValuesOrderedByHeader = new ArrayList<>();
                    for (String header : headers) {
                        Object theValue = map.get(header);
                        recordWithValuesOrderedByHeader.add(theValue);
                    }
                    csvPrinter.printRecord(recordWithValuesOrderedByHeader);
                } else {
                    csvPrinter.printRecord(map.values());
                }
            } else {
                // Single valued record
                csvPrinter.printRecord(object);
            }
        }

        csvPrinter.flush();
    }
}
