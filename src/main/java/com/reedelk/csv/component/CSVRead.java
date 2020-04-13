package com.reedelk.csv.component;

import com.reedelk.csv.internal.CSVDataRow;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.commons.DynamicValueUtils;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.DataRow;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.api.commons.StringUtils.isNotNull;
import static java.util.stream.Collectors.toList;

@ModuleComponent("CSV Read")
@Component(service = CSVRead.class, scope = ServiceScope.PROTOTYPE)
public class CSVRead implements ProcessorSync {

    @Property("CSV Format")
    @DefaultValue("DEFAULT")
    @Example("MONGODB_CSV")
    private Format format;

    @Property("CSV File")
    private DynamicString file;

    @Property("Delimiter")
    @Group("Advanced")
    private String delimiter;

    @Property("First record as header")
    @Group("Advanced")
    private Boolean firstRecordAsHeader;

    @Property("Allow missing column names")
    @Group("Advanced")
    private Boolean allowMissingColumnNames;

    @Property("First record as header")
    @Group("Advanced")
    private Boolean allowDuplicateHeaderNames;

    @Property("Trim")
    @Group("Advanced")
    private Boolean trim;

    @Property("Ignore empty lines")
    @Group("Advanced")
    private Boolean ignoreEmptyLines;

    @Reference
    ConverterService converter;
    @Reference
    ScriptEngineService scriptService;

    @Override
    public Message apply(FlowContext flowContext, Message message) {

        CSVFormat format = Optional.ofNullable(this.format).orElse(Format.DEFAULT).format();
        if (isNotNull(delimiter)) {
            format = format.withDelimiter(delimiter.charAt(0));
        }
        if (firstRecordAsHeader != null && firstRecordAsHeader) {
            format = format.withFirstRecordAsHeader();
        }
        if (allowMissingColumnNames != null && allowMissingColumnNames) {
            format = format.withAllowMissingColumnNames();
        }
        if (trim != null && trim) {
            format = format.withTrim();
        }
        if (ignoreEmptyLines != null && ignoreEmptyLines) {
            format = format.withIgnoreEmptyLines();
        }

        Reader in;
        if (DynamicValueUtils.isNotNullOrBlank(file)) {
            // We take it from the payload.
            Optional<String> maybeFilePathAndName = scriptService.evaluate(file, flowContext, message);
            String filePathAndName = maybeFilePathAndName.orElseThrow(() -> {
                throw new PlatformException("File was empty.");
            });
            try {
                in = new FileReader(filePathAndName);
            } catch (FileNotFoundException exception) {
                throw new PlatformException(exception.getMessage(), exception);
            }

        } else {
            // We must convert the payload to a string.
            Object payload = message.payload();
            String payloadAsString = converter.convert(payload, String.class);
            in = new StringReader(payloadAsString);
        }

        // TODO: If first headers..then start from 1, otherwise not...

        try {
            CSVParser parse = format.parse(in);
            List<CSVRecord> records = parse.getRecords();
            List<String> headerNames = parse.getHeaderNames();

            List<DataRow> mapRows = csvRecordsToRows(records, headerNames);

            // TODO: Add attributes
            return MessageBuilder.get()
                    .withList(mapRows, DataRow.class)
                    .build();
        } catch (IOException exception) {
            throw new PlatformException(exception.getMessage(), exception);
        }
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

    public void setFirstRecordAsHeader(Boolean firstRecordAsHeader) {
        this.firstRecordAsHeader = firstRecordAsHeader;
    }

    public void setAllowMissingColumnNames(Boolean allowMissingColumnNames) {
        this.allowMissingColumnNames = allowMissingColumnNames;
    }

    public void setAllowDuplicateHeaderNames(Boolean allowDuplicateHeaderNames) {
        this.allowDuplicateHeaderNames = allowDuplicateHeaderNames;
    }

    public void setTrim(Boolean trim) {
        this.trim = trim;
    }

    public void setIgnoreEmptyLines(Boolean ignoreEmptyLines) {
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    private List<DataRow> csvRecordsToRows(List<CSVRecord> records, List<String> headerNames) {
        return records.stream().map(record -> {
            List<String> rowData = new ArrayList<>();
            for (int i = 0; i < record.size(); i++) {
                rowData.add(record.get(i));
            }
            return new CSVDataRow(headerNames, rowData);
        }).collect(toList());
    }
}
