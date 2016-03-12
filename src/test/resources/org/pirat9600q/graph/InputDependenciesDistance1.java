package pirat9600q.graph;

public class InputDependenciesDistance1 {

    void a() {
        a1(); //1
        a2(); //2
        a3(); //3
        a3(); //0
        a3(); //0
        a3(); //0
        a3(); //0
        a3(); //0
        b();  //4
    }

    void a1() { }

    void a2() { }

    void a3() { }

    void b() {
        b2(); //2
    }

    void b1() { }

    void b2() { }
}
