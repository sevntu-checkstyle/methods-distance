package com.github.sevntu.checkstyle.dot;

public enum Colors {

    GREEN("#00ff00"),

    YELLOW("#ffff00"),

    BLUE("#0000ff"),

    BLACK("#000000");

    private final String rgb;

    Colors(String rgb) {
        this.rgb = rgb;
    }

    public final String asString() {
        return rgb;
    }
}
