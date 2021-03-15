package de.codecentric.reedelk.csv.internal.exception;

import de.codecentric.reedelk.runtime.api.exception.PlatformException;

public class CSVReadException extends PlatformException {

    public CSVReadException(String message) {
        super(message);
    }

    public CSVReadException(String message, Throwable exception) {
        super(message, exception);
    }
}

