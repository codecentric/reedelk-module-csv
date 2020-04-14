package com.reedelk.csv.internal;

import com.reedelk.csv.component.Format;
import com.reedelk.csv.internal.commons.Utils;
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

    public CSVFormatBuilder trim(Boolean trim) {
        this.trim = trim;
        return this;
    }

    public CSVFormatBuilder format(Format format) {
        this.format = format;
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
        if (Utils.isTrue(firstRecordAsHeader)) format = format.withFirstRecordAsHeader();
        if (Utils.isTrue(trim)) format = format.withTrim();

        return format;
    }
}
