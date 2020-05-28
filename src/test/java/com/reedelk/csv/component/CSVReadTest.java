package com.reedelk.csv.component;

import com.reedelk.csv.internal.type.CSVRecord;
import com.reedelk.runtime.api.commons.ModuleContext;
import com.reedelk.runtime.api.converter.ConverterService;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessageBuilder;
import com.reedelk.runtime.api.message.content.MimeType;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CSVReadTest {

    private static final List<String> HEADERS =
            asList("Player Name", "Position", "Nicknames", "Years Active");

    @Mock
    private FlowContext context;
    @Mock
    private ConverterService converter;
    @Mock
    private ScriptEngineService scriptService;

    private CSVRead csvRead;

    @BeforeEach
    void setUp() {
        csvRead = new CSVRead();
        csvRead.converter = converter;
        csvRead.scriptService = scriptService;

        lenient().doAnswer(invocation -> invocation.getArgument(0))
                .when(converter)
                .convert(any(Object.class), any(Class.class));
    }

    @Test
    void shouldCorrectlyReadCSVFromPayloadWhenFirstRecordIsHeader() {
        // Given
        csvRead.setFirstRecordAsHeader(true);
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_HEADER.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<CSVRecord> records = actual.payload();

        assertThat(records).hasSize(3);

        assertExistRecord(records, HEADERS,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records, HEADERS,
                asList("Bud Grimsby","Center Field","\"The Reaper\", \"Longneck\"","1910-1917"));
        assertExistRecord(records, HEADERS,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    @Test
    void shouldCorrectlyReadCSVFromPayloadWhenFirstRecordIsNotHeader() {
        // Given
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITHOUT_HEADER.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<List<String>> records = actual.payload();

        assertThat(records).hasSize(3);

        assertExistRecord(records,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records,
                asList("Bud Grimsby","Center Field","\"The Reaper\", \"Longneck\"","1910-1917"));
        assertExistRecord(records,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    @Test
    void shouldCorrectlyIgnoreEmptyLines() {
        // Given
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_EMPTY_LINES.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<List<String>> records = actual.payload();

        assertThat(records).hasSize(2);

        assertExistRecord(records,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    @Test
    void shouldCorrectlyTrimContent() {
        // Given
        csvRead.setTrim(true);
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_NOT_TRIMMED_CONTENT.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<List<String>> records = actual.payload();

        assertThat(records).hasSize(2);

        assertExistRecord(records,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    @Test
    void shouldReturnEmptyWhenCsvEmpty() {
        // Given
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_NO_RECORDS.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<List<String>> records = actual.payload();

        assertThat(records).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenCsvEmptyWithHeaders() {
        // Given
        csvRead.setFirstRecordAsHeader(true);
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_NO_RECORDS_AND_HEADERS.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<List<String>> records = actual.payload();

        assertThat(records).isEmpty();
    }

    @Test
    void shouldReturnCorrectRecordsWhenCustomDelimiter() {
        // Given
        csvRead.setFirstRecordAsHeader(true);
        csvRead.setDelimiter(':');
        csvRead.initialize();

        String csvContent = CSVs.SAMPLE_WITH_CUSTOM_DELIMITER.string();
        Message input = MessageBuilder.get(TestComponent.class)
                .withString(csvContent, MimeType.TEXT_PLAIN)
                .build();

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<CSVRecord> records = actual.payload();

        assertThat(records).hasSize(3);

        assertExistRecord(records, HEADERS,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records, HEADERS,
                asList("Bud Grimsby","Center Field","\"The Reaper\", \"Longneck\"","1910-1917"));
        assertExistRecord(records, HEADERS,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    @Test
    void shouldReadCSVFromFile() throws IOException {
        // Given
        String csvData = CSVs.SAMPLE_WITH_HEADER.string();
        Path tmpFilePath = createTmpFileWithData(csvData);
        DynamicString dynamicFile = DynamicString.from("#['" + tmpFilePath.toString() + "']", new ModuleContext(10L));

        csvRead.setFirstRecordAsHeader(true);
        csvRead.setFile(dynamicFile);
        csvRead.initialize();

        Message input = MessageBuilder.get(TestComponent.class)
                .empty()
                .build();

        doAnswer(invocation -> Optional.of(tmpFilePath.toString()))
                .when(scriptService)
                .evaluate(dynamicFile, context, input);

        // When
        Message actual = csvRead.apply(context, input);

        // Then
        List<CSVRecord> records = actual.payload();

        assertThat(records).hasSize(3);

        assertExistRecord(records, HEADERS,
                asList("Skippy Peterson","First Base","\"Blue Dog\", \"The Magician\"","1908-1913"));
        assertExistRecord(records, HEADERS,
                asList("Bud Grimsby","Center Field","\"The Reaper\", \"Longneck\"","1910-1917"));
        assertExistRecord(records, HEADERS,
                asList("Vic Crumb","Shortstop","\"Fat Vic\", \"Icy Hot\"","1911-1912"));
    }

    private Path createTmpFileWithData(String csvData) throws IOException {
        String tmpFileName = UUID.randomUUID().toString() + ".csv";
        Path tmpFilePath = Paths.get(System.getProperty("java.io.tmpdir"), tmpFileName);
        Files.write(tmpFilePath, csvData.getBytes());
        return tmpFilePath;
    }

    private void assertExistRecord(List<List<String>> records, List<String> expected) {
        for (List<String> currentRecord : records) {
            boolean sameRecord = isSameRecord(currentRecord, expected);
            if (sameRecord) return; // found
        }
        fail("Could not find record with values=[%s]", expected);
    }

    private void assertExistRecord(List<CSVRecord> records, List<String> headers, List<String> expected) {
        for (CSVRecord currentRecord : records) {
            boolean sameRecord = isSameRecord(currentRecord, headers, expected);
            if (sameRecord) return; // found
        }
        fail("Could not find record with headers=[%s], values=[%s]", headers, expected);
    }

    private boolean isSameRecord(List<String> actual, List<String> expectedValues) {
        for (int i = 0; i < expectedValues.size(); i++) {
            String actualValue = actual.get(i);
            String expectedValue = expectedValues.get(i);
            if (!Objects.equals(actualValue, expectedValue)) return false;
        }
        return true;
    }

    private boolean isSameRecord(CSVRecord actual, List<String> expectedHeaders, List<String> expectedValues) {
        for (int i = 0; i < expectedHeaders.size(); i++) {
            String expectedHeader = expectedHeaders.get(i);
            String expectedValue = expectedValues.get(i);
            boolean containsKey = actual.containsKey(expectedHeader);
            boolean sameValue = Objects.equals(actual.get(expectedHeader), expectedValue);
            if (!(containsKey && sameValue)) return false;
        }
        return true;
    }
}
