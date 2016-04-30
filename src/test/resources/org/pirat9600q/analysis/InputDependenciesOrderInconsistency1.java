package pirat9600q.graph;

public class InputDependenciesOrderInconsistency1 {

    void a() {
        a1(); //1
        a2(); //2
    }

    void a1() { }

    void a2() { }
}
