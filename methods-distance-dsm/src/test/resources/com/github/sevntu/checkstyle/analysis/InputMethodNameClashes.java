package com.github.sevntu.checkstyle.analysis;

public class InputMethodNameClashes {

    public void method() {
        String.format("asdasd");
    }

    public void format(final String fmt) {

    }
}
