package com.github.sevntu.checkstyle.dot;

import java.util.List;
import java.util.stream.Collectors;

public interface CompositeElement extends Element {

    List<Element> components();

    default void addComponent(Element component) {
        components().add(component);
    }

    default String serializeComponents() {
        return components().stream()
            .map(Element::serialize).collect(Collectors.joining());
    }
}
