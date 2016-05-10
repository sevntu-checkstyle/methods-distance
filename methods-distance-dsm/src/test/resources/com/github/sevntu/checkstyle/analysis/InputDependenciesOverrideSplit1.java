package com.github.sevntu.checkstyle.analysis;

public class InputDependenciesOverrideSplit1 {

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    void a() { }

    void b() { }

    void c() { }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    void d() { }
}
