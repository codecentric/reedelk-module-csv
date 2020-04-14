package com.reedelk.csv.internal.write;

import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.message.content.ListContent;
import com.reedelk.runtime.api.message.content.TypedContent;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.List;

public class CSVWriter {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(Message message, CSVPrinter csvPrinter) throws IOException {
        // We must have a list of lists
        TypedContent<?,?> payload = message.content();
        if (!(payload instanceof ListContent)) {
            throw new PlatformException("Payload must be list content");
        }

        ListContent<Object> list = (ListContent<Object>) payload;
        for (Object object : list.data()) {
            if (object instanceof List) {
                // If include headers, we write the first X items from the list.
                csvPrinter.printRecord((List<Object>) object);

            } else if (object instanceof DataRow) {
                // IF headers, we get the headers, if not found then nothing
                DataRow row = (DataRow) object;
                csvPrinter.printRecord(row.values());
            } else {
                throw new PlatformException("Content type not supported");
            }
        }
        csvPrinter.flush();
    }
}
