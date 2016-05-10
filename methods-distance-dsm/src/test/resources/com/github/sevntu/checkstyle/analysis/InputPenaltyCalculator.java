package com.github.sevntu.checkstyle.analysis;

public class InputPenaltyCalculator {

    //override group
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    void method1() { }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    void ol1(String s) { }

    // overload group division
    void method2() { }

    void ol1(Integer i) { }

    //declaration before usage
    void a() { }

    void b() {
        a();
    }
}
