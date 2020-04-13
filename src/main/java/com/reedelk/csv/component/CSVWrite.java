package com.reedelk.csv.component;

import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@ModuleComponent("CSV Write")
@Component(service = CSVWrite.class, scope = ServiceScope.PROTOTYPE)
public class CSVWrite implements ProcessorSync {

    @Property("CSV File Out")
    @Hint("/var/files/csv/my-csv-file.csv")
    @Description("File to write the CSV to")
    private DynamicString file;

    @Property("Delimiter")
    @Hint(",")
    @Example(",")
    @DefaultValue(",")
    @Description("The delimiter to be used to separate data")
    private String delimiter;

    @Property("Include header")
    @DefaultValue("false")
    @Description("If true headers are included in the first row")
    private Boolean includeHeaders;

    @Property("Headers")
    @TabGroup("Headers")
    @When(propertyName = "includeHeaders", propertyValue = "true")
    private List<String> headers;

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        // TODO If the payload is list, we write the list [[1,2,3],[1,2,3],[4,5,6]
        // TODO: Would write into a file or as output in the palyoad.
        // If we write to a file we directly use the file.
        CSVFormat format = CSVFormat.DEFAULT;
        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {
            List<Object> payload = message.payload();
// Throw exception if this one is not a flux or something
            for (Object object : payload) {
                if (object instanceof List) {
                    csvPrinter.printRecord((List<Object>) object);
                }
                if (object instanceof DataRow) {
                    DataRow row = (DataRow) object;
                    csvPrinter.printRecord(row.row());
                }
            }

            csvPrinter.flush();

            String csv = writer.toString();

// RENAME: TEXT_COMMA_SEPARATED to TEXT_CSV
            return MessageBuilder.get()
                    .withString(csv, MimeType.TEXT_CSV)
                    .build();
        } catch (IOException exception) {
            throw new PlatformException(exception);
        }
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setIncludeHeaders(Boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }
}
