package com.reedelk.csv.component;

import com.reedelk.csv.internal.read.CSVFormatBuilder;
import com.reedelk.csv.internal.read.CSVParser;
import com.reedelk.csv.internal.read.CSVReadAttribute;
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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.*;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@ModuleComponent("CSV Read")
@Component(service = CSVRead.class, scope = ServiceScope.PROTOTYPE)
public class CSVRead implements ProcessorSync {

    @Property("CSV Format")
    @DefaultValue("DEFAULT")
    @Example("MONGODB_CSV")
    private Format format;

    @Property("CSV Input file")
    @Hint("/var/files/csv/my-csv-file.csv")
    @Description("File to read the CSV data from")
    private DynamicString file;

    @Property("CSV Delimiter")
    @Hint(",")
    @Example(",")
    @DefaultValue(",")
    @Description("The delimiter used in the input data to separate the data on each row.")
    private String delimiter;

    @Property("Trim")
    @Example("true")
    @DefaultValue("false")
    @Description("If true leading and trailing blanks are trimmed for each item in the data.")
    private Boolean trim;

    @Property("Ignore empty lines")
    @Example("true")
    @DefaultValue("false")
    @Description("If true empty lines are ignored from the CSV data.")
    private Boolean ignoreEmptyLines;

    @Property("First record as header")
    @Example("true")
    @DefaultValue("false")
    @Description("Set this value to true if the CSV data contains data headers in the first line. " +
            "Data header names can be used to retrieve data from the output data structure.")
    private Boolean firstRecordAsHeader;

    @Reference
    ConverterService converter;
    @Reference
    ScriptEngineService scriptService;

    private CSVFormat csvFormat;

    @Override
    public void initialize() {
        csvFormat = CSVFormatBuilder.get()
                .firstRecordAsHeader(firstRecordAsHeader)
                .ignoreEmptyLines(ignoreEmptyLines)
                .delimiter(delimiter)
                .format(format)
                .trim(trim)
                .build();
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        if (DynamicValueUtils.isNotNullOrBlank(file)) {
            return readFromFile(flowContext, message);
        } else {
            return readFromMessagePayload(message);
        }
    }

    /**
     * Note that we must convert the payload into a string if it is not already.
     */
    @SuppressWarnings("rawtypes")
    private Message readFromMessagePayload(Message message) {
        // We must convert the payload into a string if it is not already.
        Object payload = message.payload();
        String payloadAsString = converter.convert(payload, String.class);

        try (Reader input = new StringReader(payloadAsString)) {
            List<DataRow> dataRows = CSVParser.from(csvFormat, input, firstRecordAsHeader);
            return MessageBuilder.get()
                    .withList(dataRows, DataRow.class, MimeType.TEXT_CSV)
                    .attributes(new DefaultMessageAttributes(CSVRead.class, ImmutableMap.of()))
                    .build();
        } catch (IOException exception) {
            throw new PlatformException("Error");
        }
    }

    private Message readFromFile(FlowContext flowContext, Message message) {
        // We take it from the payload.
        String filePathAndName = scriptService.evaluate(file, flowContext, message)
                .orElseThrow(() -> {
                    throw new PlatformException("File was empty.");
                });

        try (Reader input = new FileReader(filePathAndName)) {
            List<DataRow> dataRows = CSVParser.from(csvFormat, input, firstRecordAsHeader);

            Map<String, Serializable> componentAttributes = ImmutableMap.of();
            componentAttributes.put(CSVReadAttribute.FILE_NAME, filePathAndName);

            return MessageBuilder.get()
                    .withList(dataRows, DataRow.class, MimeType.TEXT_CSV)
                    .attributes(new DefaultMessageAttributes(CSVRead.class, componentAttributes))
                    .build();
        } catch (IOException exception) {
            throw new PlatformException(exception.getMessage(), exception);
        }
    }

    public void setTrim(Boolean trim) {
        this.trim = trim;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setIgnoreEmptyLines(Boolean ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    public void setFirstRecordAsHeader(Boolean firstRecordAsHeader) {
        this.firstRecordAsHeader = firstRecordAsHeader;
    }
}
