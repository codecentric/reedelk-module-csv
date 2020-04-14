package com.reedelk.csv.internal.exception;

import com.reedelk.runtime.api.exception.PlatformException;

public class CSVWriteException extends PlatformException {

    public CSVWriteException(String message) {
        super(message);
    }

    public CSVWriteException(String message, Throwable exception) {
        super(message, exception);
    }
}
