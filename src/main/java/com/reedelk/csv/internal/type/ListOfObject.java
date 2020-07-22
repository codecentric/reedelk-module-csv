package com.reedelk.csv.internal.type;

import com.reedelk.runtime.api.annotation.Type;

import java.util.ArrayList;

@Type(listItemType = Object.class)
public class ListOfObject extends ArrayList<Object> {
}
