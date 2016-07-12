package com.github.sevntu.checkstyle.dot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cluster implements CompositeElement {

    private final String name;

    private final List<Element> componentList = new ArrayList<>();

    public Cluster(String name) {
        this.name = name;
    }

    @Override
    public List<Element> components() {
        return componentList;
    }

    @Override
    public String serialize() {
        final String componentsContent = components().stream()
            .map(Element::serialize).collect(Collectors.joining());
        return String.format("subgraph cluster%s {\n%s}\n", name, componentsContent);
    }
}
