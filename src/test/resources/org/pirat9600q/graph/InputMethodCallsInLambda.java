package org.pirat9600q.graph;

public class InputMethodCallsInLambda {

    private Runnable r = () -> {dependency();};

    public void m() {
        final Runnable r = () -> dependency();
    }

    public void dependency() {}
}
