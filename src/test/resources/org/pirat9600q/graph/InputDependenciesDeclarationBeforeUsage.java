package pirat9600q.graph;

public class InputDependenciesDeclarationBeforeUsage {

    void c() { }

    void d() {
        c();
        c();
    }

    void e() {
        c();
    }
}
