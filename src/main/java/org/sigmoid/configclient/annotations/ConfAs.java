package org.sigmoid.configclient.annotations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConfAs {

    NORMAL("normal"),

    JSON("json"),

    LIST("list"),

    MAP("map");

    private String value;

}
