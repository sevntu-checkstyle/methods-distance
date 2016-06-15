package com.github.sevntu.checkstyle.reordering;

import com.github.sevntu.checkstyle.ordering.Ordering;

public interface MethodReorderer {

    Ordering reorder(Ordering ordering);
}
