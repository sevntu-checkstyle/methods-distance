package com.github.sevntu.checkstyle.dot.domain;

import java.util.ArrayList;
import java.util.List;

public class Graph implements CompositeElement {

    private final String name;

    private final List<Element> componentList = new ArrayList<>();

    private Rankdirs rankdir = Rankdirs.LR;

    public Graph(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Rankdirs getRankdir() {
        return rankdir;
    }

    public void setRankdir(Rankdirs rankdir) {
        this.rankdir = rankdir;
    }

    @Override
    public List<Element> components() {
        return componentList;
    }
}
