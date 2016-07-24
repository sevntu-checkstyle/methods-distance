package com.github.sevntu.checkstyle.domain;

public class InputVarargMethodCall {

    public void c1() {
        varargMethod(1);
    }

    public void c2() {
        varargMethod(1, 2, 3);
    }

    public void c3() {
        varargMethod();
    }

    public void varargMethod(Integer... ii) {}
}
