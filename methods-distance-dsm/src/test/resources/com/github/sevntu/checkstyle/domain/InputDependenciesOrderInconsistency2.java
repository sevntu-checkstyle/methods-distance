package com.github.sevntu.checkstyle.domain;

public class InputDependenciesOrderInconsistency2 {

    void a() {
        a2(); //2
        a1(); //1
        a1();
        a1();
        a3(); //3
    }

    void a1() { }

    void a2() { }

    void a3() { }
}
