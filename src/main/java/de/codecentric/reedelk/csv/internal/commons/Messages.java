package de.codecentric.reedelk.csv.internal.commons;

import de.codecentric.reedelk.runtime.api.commons.FormattedMessage;

public class Messages {

    private Messages() {
    }

    public enum CSVRead implements FormattedMessage {

        FILE_READ_ERROR("Could not read from CSV file=[%s], cause=[%s]."),
        FILE_PATH_EMPTY("Could not read from CSV file. The file path was empty (DynamicValue=[%s])."),
        PAYLOAD_READ_ERROR("Could not read CSV payload, cause=[%s].");

        private String message;

        CSVRead(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }

    public enum CSVWrite implements FormattedMessage {

        FILE_WRITE_ERROR("Could not write to CSV file=[%s], cause=[%s]."),
        FILE_PATH_EMPTY("Could not write to CSV file. The file path was empty (DynamicValue=[%s])."),
        PAYLOAD_WRITE_ERROR("Could not write CSV into message payload, cause=[%s]."),
        PAYLOAD_TYPE_ERROR("The payload must contain a list, but type=[%s] was given.");

        private String message;

        CSVWrite(String message) {
            this.message = message;
        }

        @Override
        public String template() {
            return message;
        }
    }
}
