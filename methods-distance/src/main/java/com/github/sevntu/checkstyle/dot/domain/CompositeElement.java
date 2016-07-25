package com.github.sevntu.checkstyle.dot.domain;

import java.util.List;

public interface CompositeElement extends Element {

    List<Element> components();

    default void addComponent(Element component) {
        components().add(component);
    }
}
