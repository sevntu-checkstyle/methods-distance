package com.github.sevntu.checkstyle.analysis;

import java.util.List;

public class InputMethodSignatures {

    public void m() {}

    public void m(boolean b) {}

    public void m(char c) {}

    public void m(byte b) {}

    public void m(short s) {}

    public void m(int i) {}

    public void m(long l) {}

    public void m(double d) {}

    public void m(String s) {}

    public void m(String s1, String s2) {}

    public void m(String s, Integer ...ii) {}

    public void m(Integer ...ii) {}

    public void m(int[] ii) {}

    public void m(Long[] ai) {}

    public void m(List<Integer> li) {}

    public void m(String s, List<Integer> ...lli) {}

    public void m(List<Integer>[] lli) {}

    public void m(List<Integer>[] ...llli) {}
}
