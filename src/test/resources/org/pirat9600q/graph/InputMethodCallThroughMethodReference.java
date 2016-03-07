package org.pirat9600q.graph;

import java.util.Arrays;
import java.util.stream.Collectors;

public class InputMethodCallThroughMethodReference {


    public void a() {
        Arrays.stream(new Integer[]{1,2,3})
            .filter(InputMethodCallThroughMethodReference::a1);
    }

    public boolean a1() { return true; }

    public static boolean a1(Integer i) { return true; }

    public void b() {
        Arrays.stream(new Integer[]{1,2,3})
            .filter(this::b1);
    }

    public static boolean b1() { return true; }

    public boolean b1(Integer i) { return true; }
}
