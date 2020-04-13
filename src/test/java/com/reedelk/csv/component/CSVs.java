package com.reedelk.csv.component;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;


public enum CSVs {

    SAMPLE() {
        @Override
        public String path() {
            return "/example.csv";
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
