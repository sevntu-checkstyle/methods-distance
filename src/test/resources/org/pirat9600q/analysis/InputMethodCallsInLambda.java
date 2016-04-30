package org.pirat9600q.analysis;

public class InputMethodCallsInLambda {

    private Runnable r = () -> {dependency();};

    public void m() {
        final Runnable r = () -> dependency();
    }

    public void dependency() {}
}
