package de.codecentric.reedelk.csv.component;

import de.codecentric.reedelk.csv.internal.CSVFormatBuilder;
import de.codecentric.reedelk.csv.internal.attribute.CSVAttributes;
import de.codecentric.reedelk.csv.internal.exception.CSVWriteException;
import de.codecentric.reedelk.csv.internal.type.ListOfListOfObject;
import de.codecentric.reedelk.csv.internal.type.ListOfMap;
import de.codecentric.reedelk.csv.internal.type.ListOfObject;
import de.codecentric.reedelk.csv.internal.write.CSVWriter;
import de.codecentric.reedelk.runtime.api.annotation.*;
import de.codecentric.reedelk.runtime.api.commons.DynamicValueUtils;
import de.codecentric.reedelk.runtime.api.component.ProcessorSync;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessageBuilder;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;
import de.codecentric.reedelk.runtime.api.script.ScriptEngineService;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import de.codecentric.reedelk.csv.internal.commons.Messages;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static de.codecentric.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireNotNull;
import static de.codecentric.reedelk.runtime.api.commons.ComponentPrecondition.Configuration.requireTrue;

@ModuleComponent("CSV Write")
@ComponentOutput(
        attributes = CSVAttributes.class,
        payload = { Void.class, String.class },
        description = "The CSV data as string if the CSV output file expression was not specified, otherwise null.")
@ComponentInput(
        payload = { ListOfListOfObject.class, ListOfMap.class, ListOfObject.class },
        description = "The input payload is used to evaluate the CSV Output File expression.")
@Description("The CSV Write component can write CSV data to the file system " +
        "or into the message payload data. There are several supported CSV formats such as " +
        "Excel, MongoDB and MySQL. The component allows to configure the data delimiter " +
        "and whether to add CSV headers at the beginning of the output file or not. ")
@Component(service = CSVWrite.class, scope = ServiceScope.PROTOTYPE)
public class CSVWrite implements ProcessorSync {

    @Property("CSV Format")
    @DefaultValue("DEFAULT")
    @Example("MONGODB_CSV")
    @Description("Sets the CSV format of the file to be written.")
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

    private boolean actualIncludeHeaders;

    @Override
    public void initialize() {
        csvFormat = CSVFormatBuilder.get()
                .delimiter(delimiter)
                .format(format)
                .build();

        actualIncludeHeaders = Optional.ofNullable(includeHeaders).orElse(false);
        if (actualIncludeHeaders) {
            requireNotNull(CSVWrite.class, headers, "header list must be defined and not be empty");
            requireTrue(CSVWrite.class, !headers.isEmpty(), "header list must not be empty");
        }
    }

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        if (DynamicValueUtils.isNotNullOrBlank(file)) {
            String filePathAndName =
                    scriptService.evaluate(file, flowContext, message).orElseThrow(() -> {
                        String error = Messages.CSVWrite.FILE_PATH_EMPTY.format(file.value());
                        throw new CSVWriteException(error);
                    });
            return writeToFile(message, filePathAndName);
        } else {
            return writeToMessage(message);
        }
    }

    private Message writeToMessage(Message message) {
        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

            CSVWriter.write(message, csvPrinter, actualIncludeHeaders, headers);
            String csv = writer.toString();
            return MessageBuilder.get(CSVWrite.class)
                    .withString(csv, MimeType.TEXT_CSV)
                    .build();

        } catch (IOException exception) {
            String error = Messages.CSVWrite.PAYLOAD_WRITE_ERROR.format(exception.getMessage());
            throw new CSVWriteException(error, exception);
        }
    }

    private Message writeToFile(Message message, String filePathAndName) {
        try (FileWriter writer = new FileWriter(filePathAndName);
             CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            CSVWriter.write(message, csvPrinter, actualIncludeHeaders, headers);

            MessageAttributes attributes = new CSVAttributes(filePathAndName);

            return MessageBuilder.get(CSVWrite.class)
                    .attributes(attributes)
                    .empty()
                    .build();

        } catch (IOException exception) {
            String error = Messages.CSVWrite.FILE_WRITE_ERROR.format(exception.getMessage(), exception.getMessage());
            throw new CSVWriteException(error, exception);
        }
    }

    public void setIncludeHeaders(Boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setFile(DynamicString file) {
        this.file = file;
    }
}
