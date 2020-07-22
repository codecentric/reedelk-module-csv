package com.reedelk.csv.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = MapOfStringObject.class)
public class ListOfMap extends ArrayList<MapOfStringObject> {
}
