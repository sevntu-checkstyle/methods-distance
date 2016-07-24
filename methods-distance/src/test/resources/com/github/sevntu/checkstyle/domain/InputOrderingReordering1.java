package com.github.sevntu.checkstyle.domain;

public class InputOrderingReordering1 {

    void a() { }

    void a(String s) { }

    void a(Integer i) { }

    void b() { } // 3

    void a(Object o) { }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    void c() { } // 6

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    void d() {
        d1();
        d2();
        d3();
    }

    void d3() { } // 9

    void d1() { }

    void d2() { }

    void setH(int h) { }

    void e() { } // 13

    int getH() { return 0; }

    void f() { } // 15

    void g() {
        f();
    }

    void h() { // 17
        i();



    }

    void i() { }
}
