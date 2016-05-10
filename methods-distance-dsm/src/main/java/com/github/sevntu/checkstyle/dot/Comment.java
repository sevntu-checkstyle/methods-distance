package com.github.sevntu.checkstyle.dot;

public class Comment implements Element {

    private final String text;

    public Comment(final String text) {
        this.text = text;
    }

    @Override
    public String serialize() {
        return String.format("/*\n%s\n*/\n", text);
    }
}
