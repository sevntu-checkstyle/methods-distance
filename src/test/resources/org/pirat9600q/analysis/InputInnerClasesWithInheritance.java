package org.pirat9600q.analysis;

public class InputInnerClasesWithInheritance {

    void method() {}

    class InnerBase {
        void method() {}
    }

    void instanceMethodCase() {
        class Inner extends InnerBase {
            void innerMethod() {
                // call to InnerBase.method(), not to InputInnerClasesWithInheritance.method()
                method();
            }
        }
    }

    static void staticMethod() {}

    static class InnerStaticBase {
        static void staticMethod() {}
    }

    void staticMethodCase() {
        class InnerStatic extends InnerStaticBase {
            void method() {
                //call to InnerStaticBase.staticMethod(), not to InputInnerClasesWithInheritance.staticMethod()
                staticMethod();
            }
        }
    }

}
