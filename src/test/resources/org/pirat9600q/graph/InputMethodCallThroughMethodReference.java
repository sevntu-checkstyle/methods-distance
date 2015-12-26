package org.pirat9600q.graph;

import java.util.Arrays;
import java.util.stream.Collectors;

public class InputMethodCallThroughMethodReference {


    public long a() {
        return Arrays.stream(new Integer[]{1,2,3})
                .filter(InputMethodCallThroughMethodReference::filter)
                .collect(Collectors.counting());
    }

    public static boolean filter(final Integer i) {
        return true;
    }
}
