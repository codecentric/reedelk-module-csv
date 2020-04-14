package com.reedelk.csv.internal;

import com.reedelk.csv.component.Format;
import com.reedelk.csv.internal.commons.Utils;
import com.reedelk.runtime.api.component.Component;
import org.apache.commons.csv.CSVFormat;

import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireTrue;

public class CSVFormatBuilder {

    private final Class<? extends Component> componentClazz;

    private Format format;
    private Character delimiter;
    private List<String> headers;

    private Boolean trim;
    private Boolean includeHeaders;
    private Boolean firstRecordAsHeader;

    private CSVFormatBuilder(Class<? extends Component> componentClazz) {
        this.componentClazz = componentClazz;
    }

    public static CSVFormatBuilder get(Class<? extends Component> componentClazz) {
        return new CSVFormatBuilder(componentClazz);
    }

    public CSVFormatBuilder trim(Boolean trim) {
        this.trim = trim;
        return this;
    }

    public CSVFormatBuilder format(Format format) {
        this.format = format;
        return this;
    }

    public CSVFormatBuilder headers(List<String> headers) {
        this.headers = headers;
        return this;
    }

    public CSVFormatBuilder delimiter(Character delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public CSVFormatBuilder includeHeaders(Boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
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
        if (Utils.isTrue(includeHeaders)) {
            requireNotNull(componentClazz, headers, "Headers must be defined");
            requireTrue(componentClazz, !headers.isEmpty(), "Headers must be specified");
            format.withHeader(headers.toArray(new String[]{}));
        }

        return format;
    }
}
