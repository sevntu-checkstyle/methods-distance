package com.github.sevntu.checkstyle.analysis;

public class InputMutuallyRecursiveMethods {

    public void a() {
        b();
    }

    public void b() {
        a();
    }
}
