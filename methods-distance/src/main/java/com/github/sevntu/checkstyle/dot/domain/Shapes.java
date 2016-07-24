package com.github.sevntu.checkstyle.dot.domain;

public enum Shapes {

    POLYGON,

    RECTANGLE,

    TRAPEZIUM,

    TRIANGLE,

    INVTRIANGLE,

    ELLIPSE;

    public final String asString() {
        return toString().toLowerCase();
    }
}
