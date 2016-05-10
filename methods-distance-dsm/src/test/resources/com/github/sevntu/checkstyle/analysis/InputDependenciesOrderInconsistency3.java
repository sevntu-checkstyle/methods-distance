package com.github.sevntu.checkstyle.analysis;

public class InputDependenciesOrderInconsistency3 {

    void a() {
        a1(); //1
        a2(); //2
        a4(); //4
        a3(); //3
    }

    void a1() { }

    void a2() { }

    void a3() { }

    void a4() { }
}
