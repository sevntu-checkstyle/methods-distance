package com.github.sevntu.checkstyle.domain;

public class InputDependenciesOrderInconsistency4 {

    void b() {
        b2(); //2
        b1(); //1
    }

    void b1() { }

    void b2() { }
}
