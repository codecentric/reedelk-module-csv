package de.codecentric.reedelk.csv.component;

import de.codecentric.reedelk.csv.internal.CSVFormatBuilder;
import de.codecentric.reedelk.csv.internal.attribute.CSVAttributes;
import de.codecentric.reedelk.csv.internal.exception.CSVReadException;
import de.codecentric.reedelk.csv.internal.read.CSVParserWithHeader;
import de.codecentric.reedelk.csv.internal.read.CSVParserWithoutHeader;
import de.codecentric.reedelk.csv.internal.type.CSVRecord;
import de.codecentric.reedelk.csv.internal.type.ListOfCSVRecord;
import de.codecentric.reedelk.runtime.api.annotation.*;
import de.codecentric.reedelk.runtime.api.commons.DynamicValueUtils;
import de.codecentric.reedelk.runtime.api.component.ProcessorSync;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessageBuilder;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import de.codecentric.reedelk.runtime.api.type.ListOfListOfString;
import de.codecentric.reedelk.runtime.api.type.ListOfString;
import de.codecentric.reedelk.csv.internal.commons.Messages;
import org.apache.commons.csv.CSVFormat;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;


@ModuleComponent("CSV Read")
@ComponentOutput(
        attributes = CSVAttributes.class,
        payload = { ListOfCSVRecord.class, ListOfListOfString.class },
        description = "List of CSV records containing the data read from the file system or payload.")
@ComponentInput(
        payload = String.class,
        description = "The CSV data to be parsed. If the input is not a string it will be converted to a string before parsing it into a list of CSV records.")
@Description("The CSV Read component can read a CSV file from the file system " +
        "or from the message payload data. There are several supported CSV formats such as " +
        "Excel, MongoDB and MySQL. The component allows to configure the data delimiter " +
        "and whether to consider the first record as header or not. " +
        "The output is a list of DataRow objects.")
@Component(service = CSVRead.class, scope = ServiceScope.PROTOTYPE)
public class CSVRead implements ProcessorSync {

    @Property("CSV Format")
    @DefaultValue("DEFAULT")
    @Example("MONGODB_CSV")
    @Description("Sets the CSV format of the file to be read.")
    private Format format;

    @Property("CSV Input file")
    @Hint("/var/files/csv/my-csv-file.csv")
    @Description("File to read the CSV data from")
    private DynamicString file;

    @Property("CSV Delimiter")
    @Hint(",")
    @Example(":")
    @DefaultValue(",")
    @Description("The delimiter used in the input data to separate the data on each row.")
    private Character delimiter;

    @Property("Trim")
    @Example("true")
    @DefaultValue("false")
    @Description("If true leading and trailing blanks are trimmed for each item in the data.")
    private Boolean trim;

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
                .delimiter(delimiter)
                .format(format)
                .trim(trim)
                .build();
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        if (DynamicValueUtils.isNotNullOrBlank(file)) {
            String filePathAndName = scriptService.evaluate(file, flowContext, message)
                    .orElseThrow(() -> {
                        String error = Messages.CSVRead.FILE_PATH_EMPTY.format(file.value());
                        throw new CSVReadException(error);
                    });
            return readFromFile(filePathAndName);
        } else {
            // We must convert the payload into a string if it is not already.
            Object payload = message.payload();
            String payloadAsString = converter.convert(payload, String.class);
            return readFromMessagePayload(payloadAsString);
        }
    }

    private Message readFromMessagePayload(String payloadAsString) {
        MessageAttributes attributes = new CSVAttributes();
        try (Reader input = new StringReader(payloadAsString)) {
            return parse(attributes, input);
        } catch (IOException exception) {
            String error = Messages.CSVRead.PAYLOAD_READ_ERROR.format(exception.getMessage());
            throw new CSVReadException(error, exception);
        }
    }

    private Message readFromFile(String filePathAndName) {
        MessageAttributes attributes = new CSVAttributes(filePathAndName);
        try (Reader input = new FileReader(filePathAndName)) {
            return parse(attributes, input);
        } catch (IOException exception) {
            String error = Messages.CSVRead.FILE_READ_ERROR.format(filePathAndName, exception.getMessage());
            throw new CSVReadException(error, exception);
        }
    }

    private Message parse(MessageAttributes attributes, Reader input) {
        boolean isFirstRecordHeader = Optional.ofNullable(firstRecordAsHeader).orElse(false);
        if (isFirstRecordHeader) {
            List<CSVRecord> dataRows =
                    CSVParserWithHeader.from(csvFormat, input);
            return MessageBuilder.get(CSVRead.class)
                    .withList(dataRows, CSVRecord.class)
                    .attributes(attributes)
                    .build();
        } else {
            List<ListOfString> dataRows =
                    CSVParserWithoutHeader.from(csvFormat, input);
            return MessageBuilder.get(CSVRead.class)
                    .withList(dataRows, ListOfString.class)
                    .attributes(attributes)
                    .build();
        }
    }

    public void setFirstRecordAsHeader(Boolean firstRecordAsHeader) {
        this.firstRecordAsHeader = firstRecordAsHeader;
    }

    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }

    public void setTrim(Boolean trim) {
        this.trim = trim;
    }
}
