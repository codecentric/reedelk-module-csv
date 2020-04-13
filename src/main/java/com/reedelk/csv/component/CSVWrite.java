package com.reedelk.csv.component;

import com.reedelk.runtime.api.annotation.ModuleComponent;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.annotation.TabGroup;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.PlatformException;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

@ModuleComponent("CSV Write")
@Component(service = CSVWrite.class, scope = ServiceScope.PROTOTYPE)
public class CSVWrite implements ProcessorSync {

    @Property("Headers")
    @TabGroup("Headers")
    private List<String> headers;

    @Override
    public Message apply(FlowContext flowContext, Message message) {
        // TODO If the payload is list, we write the list [[1,2,3],[1,2,3],[4,5,6]
        // TODO: Would write into a file or as output in the palyoad.
        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("ID", "Name", "Designation", "Company"));
        )
        {
            // IF A collection then
            csvPrinter.printRecord("1", "Sundar Pichai ♥", "CEO", "Google");
            csvPrinter.printRecord("2", "Satya Nadella", "CEO", "Microsoft");
            csvPrinter.printRecord("3", "Tim cook", "CEO", "Apple");

            csvPrinter.printRecord(Arrays.asList("4", "Mark Zuckerberg", "CEO", "Facebook"));

            csvPrinter.flush();

            return null;
        } catch (IOException exception) {
            throw new PlatformException(exception);
        }
    }
}
