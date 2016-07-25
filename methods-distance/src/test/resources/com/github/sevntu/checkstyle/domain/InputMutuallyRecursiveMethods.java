package com.github.sevntu.checkstyle.domain;

public class InputMutuallyRecursiveMethods {

    public void a() {
        b();
    }

    public void b() {
        a();
    }
}
