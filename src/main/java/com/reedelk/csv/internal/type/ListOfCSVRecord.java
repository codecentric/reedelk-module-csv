package com.reedelk.csv.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = CSVRecord.class)
public class ListOfCSVRecord extends ArrayList<CSVRecord> {
}
