package com.github.sevntu.checkstyle.analysis;

public interface DependencyInformationConsumer {
    void accept(String filePath, Dependencies dependencies);
}
