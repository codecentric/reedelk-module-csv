package de.codecentric.reedelk.csv.internal.commons;

import de.codecentric.reedelk.csv.internal.exception.CSVWriteException;
import de.codecentric.reedelk.runtime.api.message.content.ListContent;
import de.codecentric.reedelk.runtime.api.message.content.TypedContent;

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
