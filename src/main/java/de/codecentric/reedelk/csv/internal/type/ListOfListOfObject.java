package de.codecentric.reedelk.csv.internal.type;

import de.codecentric.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = ListOfObject.class)
public class ListOfListOfObject extends ArrayList<ListOfObject> {
}
