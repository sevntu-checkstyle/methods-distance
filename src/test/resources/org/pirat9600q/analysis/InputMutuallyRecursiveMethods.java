package org.pirat9600q.analysis;

public class InputMutuallyRecursiveMethods {

    public void a() {
        b();
    }

    public void b() {
        a();
    }
}
