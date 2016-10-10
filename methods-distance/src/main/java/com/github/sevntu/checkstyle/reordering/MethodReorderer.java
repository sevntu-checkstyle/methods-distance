package com.github.sevntu.checkstyle.reordering;

import com.github.sevntu.checkstyle.ordering.MethodOrder;

public interface MethodReorderer {

    MethodOrder reorder(MethodOrder methodOrder);
}
