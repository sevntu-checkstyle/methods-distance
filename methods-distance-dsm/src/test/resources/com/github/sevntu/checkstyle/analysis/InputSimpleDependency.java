package com.github.sevntu.checkstyle.analysis;

public class InputSimpleDependency {

    public InputSimpleDependency() {
        dependant();
    }

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
