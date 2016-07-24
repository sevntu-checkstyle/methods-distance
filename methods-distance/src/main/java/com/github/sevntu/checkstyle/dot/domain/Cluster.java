package com.github.sevntu.checkstyle.dot.domain;

import java.util.ArrayList;
import java.util.List;

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

    public String getName() {
        return name;
    }
}
