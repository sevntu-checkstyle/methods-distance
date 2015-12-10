package org.pirat9600q.graph;

public class InputSimpleDependency {

    public void dependant() {
        dependency();
    }

    public void dependency() {
        dependencyDependency1();
        dependencyDependency2();
    }

    public void dependencyDependency1() {

    }

    public void dependencyDependency2() {

    }
}
