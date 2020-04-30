package com.reedelk.csv.internal.attribute;

import com.reedelk.runtime.api.annotation.Type;
import com.reedelk.runtime.api.annotation.TypeProperty;
import com.reedelk.runtime.api.message.MessageAttributes;

import static com.reedelk.csv.internal.attribute.CSVAttributes.FILE_NAME;

@Type
@TypeProperty(name = FILE_NAME, type = String.class)
public class CSVAttributes extends MessageAttributes {

    static final String FILE_NAME = "fileName";

    public CSVAttributes(String filePathAndName) {
        put(FILE_NAME, filePathAndName);
    }

    public CSVAttributes() {
    }
}
