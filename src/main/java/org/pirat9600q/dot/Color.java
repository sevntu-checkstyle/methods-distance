package org.pirat9600q.dot;

public enum Color {

    GREEN("#00ff00"),

    YELLOW("#ffff00"),

    BLUE("#0000ff"),

    BLACK("#000000");

    private final String rgb;


    Color(String rgb) {
        this.rgb = rgb;
    }

    public final String asString() {
        return rgb;
    }
}
