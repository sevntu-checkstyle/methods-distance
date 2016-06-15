package com.github.sevntu.checkstyle.dot;

import java.util.ArrayList;
import java.util.List;

public class Graph implements CompositeElement {

    private final String name;

    private final List<Element> componentList = new ArrayList<>();

    private Rankdirs rankdir = Rankdirs.LR;

    public Graph(String name) {
        this.name = name;
    }

    public void setRankdir(Rankdirs rankdir) {
        this.rankdir = rankdir;
    }

    @Override
    public List<Element> components() {
        return componentList;
    }

    @Override
    public String serialize() {
        final String options = String.format("rankdir = \"%s\";\n", rankdir);
        return String.format("digraph \"%s\" {\n%s%s}\n", name, options, serializeComponents());
    }
}
