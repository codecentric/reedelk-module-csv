package com.reedelk.csv.internal.read;

import com.reedelk.csv.component.Format;
import org.apache.commons.csv.CSVFormat;

import java.util.Optional;

public class CSVFormatBuilder {

    private Format format;
    private Character delimiter;
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

    public CSVFormatBuilder delimiter(Character delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVFormatBuilder firstRecordAsHeader(Boolean firstRecordAsHeader) {
        this.firstRecordAsHeader = firstRecordAsHeader;
        return this;
    }

    public CSVFormat build() {
        CSVFormat format = Optional.ofNullable(this.format).orElse(Format.DEFAULT).format();

        if (delimiter != null) format = format.withDelimiter(delimiter);
        if (isTrue(firstRecordAsHeader)) format = format.withFirstRecordAsHeader();
        if (isTrue(trim)) format = format.withTrim();

        return format;
    }

    private boolean isTrue(Boolean value) {
        return value != null && value;
    }
}
