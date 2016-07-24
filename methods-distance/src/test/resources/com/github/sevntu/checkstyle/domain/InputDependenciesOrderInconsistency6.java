package com.github.sevntu.checkstyle.domain;

public class InputDependenciesOrderInconsistency6 {

    void a() {
        a2(); //2
        a3(); //3
        a5(); //5
    }

    void a1() { }

    void a2() { }

    void a3() { }

    void a4() { }

    void a5() { }
}
