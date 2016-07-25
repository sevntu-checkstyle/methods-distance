package com.github.sevntu.checkstyle.module;

import com.github.sevntu.checkstyle.domain.Dependencies;

public interface DependencyInformationConsumer {
    void accept(MethodCallDependencyModule check, String filePath, Dependencies dependencies);
}
