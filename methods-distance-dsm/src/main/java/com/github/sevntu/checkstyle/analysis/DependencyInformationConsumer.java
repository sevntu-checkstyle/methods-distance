package com.github.sevntu.checkstyle.analysis;

import com.github.sevntu.checkstyle.check.MethodCallDependencyModule;

public interface DependencyInformationConsumer {
    void accept(MethodCallDependencyModule check, String filePath, Dependencies dependencies);
}
