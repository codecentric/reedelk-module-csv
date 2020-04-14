package com.reedelk.csv.internal.commons;

import com.reedelk.csv.internal.exception.CSVWriteException;
import com.reedelk.runtime.api.message.content.ListContent;
import com.reedelk.runtime.api.message.content.TypedContent;

public class Preconditions {

    private Preconditions() {
    }

    public static void checkSuitableTypeOrThrow(TypedContent<?,?> content) {
        if (content != null && !(content instanceof ListContent)) {
            String error = Messages.CSVWrite.PAYLOAD_TYPE_ERROR.format(content.getClass().getSimpleName());
            throw new CSVWriteException(error);
        }
    }
}
