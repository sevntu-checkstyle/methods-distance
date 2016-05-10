package com.github.sevntu.checkstyle.analysis;

public class InputMethodDefinition1 {

    /* getters */

    public int getSomething() { return 0; }

    public Object getObject() { return null; }

    public boolean getBoolean() { return false; }

    public boolean isBoolean() { return false; }

    public Boolean isBoolean1() { return false; }

    /* not getters */

    public void getLogical() { }

    public boolean getReplacement(boolean b) { return false; }

    /* setters */

    public void setSomething(int i) { }

    /* not setters */

    public void setNothing() { }

    public void setMultiple(int a, int b) { }

    public void setSeveral(int... ii) { }

    public int setUpdatable(int i) { return 0; }
}
