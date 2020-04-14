package com.reedelk.csv.internal.read;

import com.reedelk.csv.component.CSVRead;
import com.reedelk.csv.component.Format;
import org.apache.commons.csv.CSVFormat;

import java.util.Optional;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireTrue;
import static com.reedelk.runtime.api.commons.StringUtils.isNotNull;

public class CSVFormatBuilder {

    private Format format;
    private String delimiter;
    private Boolean trim;
    private Boolean firstRecordAsHeader;

    private CSVFormatBuilder() {
    }

    public static CSVFormatBuilder get() {
        return new CSVFormatBuilder();
    }

    public CSVFormatBuilder format(Format format) {
        this.format = format;
        return this;
    }

    public CSVFormatBuilder trim(Boolean trim) {
        this.trim = trim;
        return this;
    }

    public CSVFormatBuilder delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVFormatBuilder firstRecordAsHeader(Boolean firstRecordAsHeader) {
        this.firstRecordAsHeader = firstRecordAsHeader;
        return this;
    }

    public CSVFormat build() {
        requireTrue(CSVRead.class, isDelimiterValid(delimiter), "Delimiter must be a single char");

        CSVFormat format = Optional.ofNullable(this.format).orElse(Format.DEFAULT).format();
        if (isNotNull(delimiter)) {
            format = format.withDelimiter(delimiter.charAt(0));
        }
        if (isTrue(firstRecordAsHeader)) {
            format = format.withFirstRecordAsHeader();
        }
        if (isTrue(trim)) {
            format = format.withTrim();
        }
        return format;
    }

    private boolean isTrue(Boolean value) {
        return value != null && value;
    }

    private boolean isDelimiterValid(String delimiter) {
        return delimiter == null || delimiter.length() == 1;
    }
}
