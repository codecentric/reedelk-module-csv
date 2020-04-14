package com.reedelk.csv.component;

import com.reedelk.csv.internal.exception.CSVWriteException;
import com.reedelk.csv.internal.read.CSVFormatBuilder;
import com.reedelk.csv.internal.write.CSVWriteAttribute;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.commons.DynamicValueUtils;
import com.reedelk.runtime.api.commons.ImmutableMap;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.DefaultMessageAttributes;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.reedelk.csv.internal.commons.Messages.CSVRead.FILE_PATH_EMPTY;

@ModuleComponent("CSV Write")
@Component(service = CSVWrite.class, scope = ServiceScope.PROTOTYPE)
public class CSVWrite implements ProcessorSync {

    @Property("CSV Format")
    @DefaultValue("DEFAULT")
    @Example("MONGODB_CSV")
    private Format format;

    @Property("CSV Output file")
    @Hint("/var/files/csv/my-csv-file.csv")
    @Description("File to write the CSV data to")
    private DynamicString file;

    @Property("CSV Delimiter")
    @Hint(",")
    @Example(":")
    @DefaultValue(",")
    @Description("The delimiter used in the input data to separate the data on each row.")
    private Character delimiter;

    @Property("Include headers")
    @DefaultValue("false")
    @Description("If true the headers specified in the 'Headers' property are included in the first CSV row.")
    private Boolean includeHeaders;

    @Property("Headers")
    @TabGroup("Headers")
    @When(propertyName = "includeHeaders", propertyValue = "true")
    private List<String> headers;

    @Reference
    ConverterService converter;
    @Reference
    ScriptEngineService scriptService;

    private CSVFormat csvFormat;

    @Override
    public void initialize() {
        csvFormat = CSVFormatBuilder.get()
                .includeHeaders(includeHeaders)
                .delimiter(delimiter)
                .headers(headers)
                .format(format)
                .build();
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        if (DynamicValueUtils.isNotNullOrBlank(file)) {

            String filePathAndName = scriptService.evaluate(file, flowContext, message)
                    .orElseThrow(() -> {
                        String error = FILE_PATH_EMPTY.format(file.value());
                        throw new CSVWriteException(error);
                    });

            try (FileWriter writer = new FileWriter(filePathAndName);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

                write(message, csvPrinter);

                Map<String, Serializable> componentAttributes =
                        ImmutableMap.of(CSVWriteAttribute.FILE_NAME, filePathAndName);

                return MessageBuilder.get()
                        .attributes(new DefaultMessageAttributes(CSVWrite.class, componentAttributes))
                        .empty()
                        .build();

            } catch (IOException exception) {
                String error = "";
                throw new CSVWriteException(error, exception);
            }

        } else {
            try (StringWriter writer = new StringWriter();
                 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

                write(message, csvPrinter);

                String csv = writer.toString();

                return MessageBuilder.get()
                        .withString(csv, MimeType.TEXT_CSV)
                        .build();

            } catch (IOException exception) {
                throw new PlatformException(exception);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void write(Message message, CSVPrinter csvPrinter) throws IOException {
        // TODO: The payload must be instance of collection.
        List<Object> payload = message.payload();
        for (Object object : payload) {
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

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }

    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    public void setIncludeHeaders(Boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
