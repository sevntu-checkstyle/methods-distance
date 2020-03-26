package com.github.sevntu.checkstyle.domain;

public abstract class InputAbstractClass {

    public InputAbstractClass() {
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

    public abstract void dependencyDependency2();
}
