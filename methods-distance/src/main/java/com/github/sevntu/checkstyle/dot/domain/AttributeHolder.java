package com.github.sevntu.checkstyle.dot.domain;

import java.util.Map;

public interface AttributeHolder {

    Map<String, String> attributes();

    default boolean hasAttributes() {
        return !attributes().isEmpty();
    }

    default void addAttribute(String name, String value) {
        attributes().put(name, value);
    }
}
