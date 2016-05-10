package com.github.sevntu.checkstyle.analysis;

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
