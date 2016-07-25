package com.github.sevntu.checkstyle.domain;

public interface InputInterfaceWithInnerClass {

    void outerMethod();

    static void outerMethod(String s) {

    }

    class InnerBase {
        void outerMethod() {

        }
    }

    class Inner extends InnerBase {

        void innerMethod() {
            outerMethod();
        }
    }
}
