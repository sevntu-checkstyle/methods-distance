package org.pirat9600q.analysis;

public interface DependencyInformationConsumer {
    void accept(String filePath, Dependencies dependencies);
}
