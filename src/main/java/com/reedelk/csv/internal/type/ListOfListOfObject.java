package com.reedelk.csv.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = ListOfObject.class)
public class ListOfListOfObject extends ArrayList<ListOfObject> {
}
