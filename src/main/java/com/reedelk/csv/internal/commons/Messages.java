package com.reedelk.csv.internal.commons;

public class Messages {

    private Messages() {
    }

    private static String formatMessage(String template, Object ...args) {
        return String.format(template, args);
    }

    interface FormattedMessage {
        String format(Object ...args);
    }

    public enum CSVRead implements FormattedMessage {

        FILE_READ_ERROR("Could not read from CSV file=[%s], cause=[%s]."),
        FILE_PATH_EMPTY("Could not read from CSV file. The file path was empty (DynamicValue=[%s])."),
        PAYLOAD_READ_ERROR("Could not read CSV payload, cause=[%s].");

        private String msg;

        CSVRead(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }

    public enum CSVWrite implements FormattedMessage {

        FILE_WRITE_ERROR("Could not write to CSV file=[%s], cause=[%s]."),
        FILE_PATH_EMPTY("Could not write to CSV file. The file path was empty (DynamicValue=[%s])."),
        PAYLOAD_WRITE_ERROR("Could not write CSV into message payload, cause=[%s]."),
        PAYLOAD_TYPE_ERROR("The payload must contain a list, but type=[%s] was given.");

        private String msg;

        CSVWrite(String msg) {
            this.msg = msg;
        }

        @Override
        public String format(Object... args) {
            return formatMessage(msg, args);
        }
    }
}
