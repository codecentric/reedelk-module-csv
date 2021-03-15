package de.codecentric.reedelk.csv.component;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;


public enum CSVs {

    SAMPLE_WITH_HEADER() {
        @Override
        public String path() {
            return "/sample_with_header.csv";
        }
    },

    SAMPLE_WITHOUT_HEADER() {
        @Override
        public String path() {
            return "/sample_without_header.csv";
        }
    },

    SAMPLE_WITH_EMPTY_LINES() {
        @Override
        public String path() {
            return "/sample_with_empty_lines.csv";
        }
    },

    SAMPLE_WITH_NOT_TRIMMED_CONTENT() {
        @Override
        public String path() {
            return "/sample_with_not_trimmed_content.csv";
        }
    },

    SAMPLE_WITH_NO_RECORDS() {
        @Override
        public String path() {
            return "/sample_with_no_records.csv";
        }
    },

    SAMPLE_WITH_NO_RECORDS_AND_HEADERS() {
        @Override
        public String path() {
            return "/sample_with_no_records_and_headers.csv";
        }
    },

    SAMPLE_WITH_CUSTOM_DELIMITER() {
        @Override
        public String path() {
            return "/sample_with_custom_delimiter.csv";
        }
    };

    public URL url() {
        return CSVs.class.getResource(path());
    }

    public abstract String path();

    public String string() {
        try (java.util.Scanner scanner = new Scanner(url().openStream(), UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            throw new RuntimeException("String from URI could not be read.", e);
        }
    }
}
