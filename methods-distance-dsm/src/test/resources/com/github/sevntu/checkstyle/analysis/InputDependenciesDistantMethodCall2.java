package com.github.sevntu.checkstyle.analysis;

public class InputDependenciesDistantMethodCall2 {

    void a() {
        b();
        c();
    }



    void b() { }

    void c() { }
}
