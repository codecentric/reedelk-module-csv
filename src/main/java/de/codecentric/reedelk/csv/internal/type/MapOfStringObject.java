package de.codecentric.reedelk.csv.internal.type;

import de.codecentric.reedelk.runtime.api.annotation.Type;

import java.util.HashMap;

@Type(mapKeyType = String.class, mapValueType = Object.class)
public class MapOfStringObject extends HashMap<String, Object> {
}
