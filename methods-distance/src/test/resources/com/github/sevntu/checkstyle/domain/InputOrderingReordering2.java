package com.github.sevntu.checkstyle.domain;

public class InputOrderingReordering2 {

    void b() { }

    void a() { }

    void a(String s) { }

    void a(Integer i) { }

    void a(Object o) { }

    void c() { }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    void d() {
        d1();
        d2();
        d3();
    }

    void d1() { }

    void d2() { }

    void d3() { }

    void setH(int h) { }

    int getH() { return 0; }

    void e() { }

    void g() {
        f();
    }

    void f() { }

    void i() {

    }

    void h() {
        i();



    }
}
