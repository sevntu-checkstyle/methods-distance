package com.github.sevntu.checkstyle.domain;

public class InputMethodCallChildOfNew {

    private void foo() {
        int[] tab = new int[bar() + 1];
        int x = new Integer(3*bar());
    }

    public int bar() {
        return 1;
    }
}
