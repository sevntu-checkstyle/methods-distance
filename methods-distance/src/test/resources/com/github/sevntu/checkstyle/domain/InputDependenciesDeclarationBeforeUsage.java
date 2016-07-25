package com.github.sevntu.checkstyle.domain;

public class InputDependenciesDeclarationBeforeUsage {

    void c() { }

    void d() {
        c();
        c();
    }

    void e() {
        c();
    }
}
