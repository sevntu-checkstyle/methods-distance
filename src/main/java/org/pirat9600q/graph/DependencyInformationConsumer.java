package org.pirat9600q.graph;

public interface DependencyInformationConsumer {
    void accept(String filePath, Dependencies dependencies);
}
