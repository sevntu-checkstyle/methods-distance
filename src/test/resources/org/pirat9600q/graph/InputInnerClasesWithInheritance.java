package org.pirat9600q.graph;

public class InputInnerClasesWithInheritance {

    void outerMethod() {

    }

    class InnerBase {
        void outerMethod() {

        }
    }

    class Inner extends InnerBase {

        void innerMethod() {
            outerMethod(); // call to InnerBase.outerMethod(), not to InputInnerClasesWithInheritance.outerMethod()
        }
    }
}
