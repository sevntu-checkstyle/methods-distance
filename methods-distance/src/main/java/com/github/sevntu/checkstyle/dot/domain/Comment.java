package com.github.sevntu.checkstyle.dot.domain;

public class Comment implements Element {

    private final String text;

    public Comment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
