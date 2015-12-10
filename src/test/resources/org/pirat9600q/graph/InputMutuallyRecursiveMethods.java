package org.pirat9600q.graph;

public class InputMutuallyRecursiveMethods {

    public void a() {
        b();
    }

    public void b() {
        a();
    }
}
