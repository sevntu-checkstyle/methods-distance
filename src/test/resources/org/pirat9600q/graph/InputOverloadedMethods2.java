package pirat9600q.graph;

public class InputOverloadedMethods2 {

    public void b1() { }

    public void b1(String s) { }

    public void b1(String s1, String s2) { }

    public void b1(String s1, String s2, String... sii) { }

    public void a1() {
        b1();
    }

    public void a2() {
        b1("");
    }

    public void a3() {
        b1("", "");
    }

    public void a4() {
        b1("", "", "");
    }

    public void c1() { }

    public void c1(String... sii) { }

    public void d1() {
        c1("", "");
    }
}
