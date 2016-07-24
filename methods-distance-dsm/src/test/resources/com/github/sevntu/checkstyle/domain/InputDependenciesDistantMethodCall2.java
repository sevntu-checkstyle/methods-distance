package com.github.sevntu.checkstyle.domain;

public class InputDependenciesDistantMethodCall2 {

    void a() {
        b();
        c();
    }



    void b() { }

    void c() { }
}
