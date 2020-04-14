package com.reedelk.csv.component;

import com.reedelk.csv.internal.CSVDataRow;
import com.reedelk.csv.internal.CSVMetadata;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.exception.ConfigurationException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.script.ScriptEngineService;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CSVWriteTest {

    @Mock
    private FlowContext context;
    @Mock
    private ConverterService converter;
    @Mock
    private ScriptEngineService scriptService;

    private CSVWrite csvWrite;

    @BeforeEach
    void setUp() {
        csvWrite = new CSVWrite();
        csvWrite.converter = converter;
        csvWrite.scriptService = scriptService;

        lenient().doAnswer(invocation -> invocation.getArgument(0))
                .when(converter)
                .convert(any(Object.class), any(Class.class));
    }

    @Test
    void shouldCorrectlyWriteIntoMessagePayloadWithoutHeaders() {
        // Given
        csvWrite.initialize();

        List<String> row1 = Arrays.asList("one", "two", "three");
        List<String> row2 = Arrays.asList("four", "five", "six");
        List<List> rows = Arrays.asList(row1, row2);

        Message input = MessageBuilder.get()
                .withList(rows, List.class)
                .build();

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo(
                "one,two,three\r\n" +
                "four,five,six\r\n");
    }

    @Test
    void shouldCorrectlyWriteIntoMessagePayloadWithHeaders() {
        // Given
        csvWrite.setIncludeHeaders(true);
        csvWrite.setHeaders(Arrays.asList("Header 1", "Header 2", "Header 3"));
        csvWrite.initialize();

        List<String> row1 = Arrays.asList("one", "two", "three");
        List<String> row2 = Arrays.asList("four", "five", "six");
        List<List> rows = Arrays.asList(row1, row2);

        Message input = MessageBuilder.get()
                .withList(rows, List.class)
                .build();

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo(
                "Header 1,Header 2,Header 3\r\n" +
                "one,two,three\r\n" +
                        "four,five,six\r\n");
    }

    @Test
    void shouldThrowExceptionWhenIncludeHeadersTrueButHeadersIsNull() {
        // Given
        csvWrite.setIncludeHeaders(true);

        // Expect
        ConfigurationException thrown = assertThrows(ConfigurationException.class,
                () -> csvWrite.initialize());

        // Then
        assertThat(thrown)
                .hasMessage("CSVWrite (com.reedelk.csv.component.CSVWrite) has a configuration error: header list must be defined and not be empty");
    }

    @Test
    void shouldThrowExceptionWhenIncludeHeadersTrueButHeadersIsEmpty() {
        // Given
        csvWrite.setHeaders(Collections.emptyList());
        csvWrite.setIncludeHeaders(true);

        // Expect
        ConfigurationException thrown = assertThrows(ConfigurationException.class,
                () -> csvWrite.initialize());

        // Then
        assertThat(thrown)
                .hasMessage("CSVWrite (com.reedelk.csv.component.CSVWrite) has a configuration error: header list must not be empty");
    }

    @Test
    void shouldWriteEmptyWhenPayloadIsNull() {
        // Given
        csvWrite.initialize();

        Message input = MessageBuilder.get()
                .empty()
                .build();

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo("");
    }

    @Test
    void shouldWriteEmptyWhenPayloadIsEmptyList() {
        // Given
        csvWrite.initialize();

        List<List> rows = Collections.emptyList();

        Message input = MessageBuilder.get()
                .withList(rows, List.class)
                .build();
        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo("");
    }

    @Test
    void shouldWriteDataRowRecordsWithoutHeaders() {
        // Given
        csvWrite.initialize();

        CSVMetadata metadata = new CSVMetadata();
        CSVDataRow row1 = new CSVDataRow(metadata, Arrays.asList("one", "two"));
        CSVDataRow row2 = new CSVDataRow(metadata, Arrays.asList("three", "four"));

        List<CSVDataRow> rows = Arrays.asList(row1, row2);

        Message input = MessageBuilder.get()
                .withList(rows, CSVDataRow.class)
                .build();

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo(
                "one,two\r\n" +
                "three,four\r\n");
    }

    @Test
    void shouldWriteDataRowRecordsWithHeaders() {
        // Given
        csvWrite.setHeaders(Arrays.asList("Header 1", "Header 3"));
        csvWrite.setIncludeHeaders(true);
        csvWrite.initialize();

        List<String> headers = Arrays.asList("Header 1", "Header 2", "Header 3");
        CSVMetadata metadata = new CSVMetadata(headers);
        CSVDataRow row1 = new CSVDataRow(metadata, Arrays.asList("one", "two", "three"));
        CSVDataRow row2 = new CSVDataRow(metadata, Arrays.asList("four", "five", "six"));

        List<CSVDataRow> rows = Arrays.asList(row1, row2);

        Message input = MessageBuilder.get()
                .withList(rows, CSVDataRow.class)
                .build();

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String csv = actual.payload();
        assertThat(csv).isEqualTo(
                "Header 1,Header 3\r\n" +
                "one,three\r\n" +
                "four,six\r\n");
    }

    @Test
    void shouldWriteDataToFile() throws IOException {
        // Given
        Path tmpFilePath = createTmpFilePath();
        DynamicString dynamicFile = DynamicString.from(tmpFilePath.toString());
        csvWrite.setHeaders(Arrays.asList("Header 1", "Header 3"));
        csvWrite.setIncludeHeaders(true);
        csvWrite.setFile(dynamicFile);
        csvWrite.initialize();

        List<String> row1 = Arrays.asList("one", "two");
        List<String> row2 = Arrays.asList("four", "five");
        List<List> rows = Arrays.asList(row1, row2);

        Message input = MessageBuilder.get()
                .withList(rows, List.class)
                .build();

        doAnswer(invocation -> Optional.of(tmpFilePath.toString()))
                .when(scriptService)
                .evaluate(dynamicFile, context, input);

        // When
        Message actual = csvWrite.apply(context, input);

        // Then
        String payload = actual.payload();
        assertThat(payload).isNull();

        String writtenCsv = new String(Files.readAllBytes(tmpFilePath));
        assertThat(writtenCsv).isEqualTo(
                "Header 1,Header 3\r\n" +
                "one,two\r\n" +
                "four,five\r\n");
    }

    private Path createTmpFilePath() {
        String tmpFileName = UUID.randomUUID().toString() + ".csv";
        return Paths.get(System.getProperty("java.io.tmpdir"), tmpFileName);
    }
}