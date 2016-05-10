package com.github.sevntu.checkstyle.analysis;

public class InputDependencies {

    void a() {
        b();
    }

    void b() {
        c();
    }

    void c() {

    }

    void d() {

    }
}
