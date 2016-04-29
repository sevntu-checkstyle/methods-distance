package org.pirat9600q.dot;

import java.util.Map;
import java.util.stream.Collectors;

public interface AttributeHolder {

    Map<String, String> attributes();

    default boolean hasAttributes() {
        return !attributes().isEmpty();
    }

    default void addAttribute(final String name, final String value) {
        attributes().put(name, value);
    }

    default String serializeAttributes() {
        return attributes().entrySet().stream()
            .map(nameAndValue ->
                String.format("%s=\"%s\"", nameAndValue.getKey(), nameAndValue.getValue()))
            .collect(Collectors.joining(" ", "[ ", " ]"));
    }
}
