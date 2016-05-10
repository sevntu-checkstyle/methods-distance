package com.github.sevntu.checkstyle.analysis;

public class InputAppearanceOrder {

    public void b1() { }

    public void b2() { }

    public void b3() { }

    public void a() {
        b3();
        b2();
        b3();
        b1();
        b2();
    }

    public void c1(String s) { }

    public String c2() { return  ""; }

    public void d() {
        c1(c2());
    }

    public void e1(String s) { }

    public String e2(Integer i) { return ""; }

    public Integer e3() { return 0; }

    public void f() {
        e1(e2(e3()));
    }
}
