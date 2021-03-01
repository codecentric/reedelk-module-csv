package de.codecentric.reedelk.csv.internal.type;

import de.codecentric.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = CSVRecord.class)
public class ListOfCSVRecord extends ArrayList<CSVRecord> {
}
