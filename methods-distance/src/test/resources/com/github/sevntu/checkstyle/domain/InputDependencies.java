package com.github.sevntu.checkstyle.domain;

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
