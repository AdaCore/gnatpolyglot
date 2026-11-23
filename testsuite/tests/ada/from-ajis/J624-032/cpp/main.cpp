#include "test.h"
#include <iostream>

class ConcreteChild : public test::Child {
    ConcreteChild(gnatpolyglot::data *d) : test::Child(d) {}
public:
    ConcreteChild() {}
    int32_t f() const override { return 42; }
    float f_1() const override { return 42.0f; }
    test::Child *internal_clone_(gnatpolyglot::data *d) override { return new ConcreteChild(d); }
};

int main() {
    // Two F overloads by return type: F(Child) return Integer disambiguated as f(),
    // F(Child) return Float disambiguated as f_1() on Root, but f_2() on Child —
    // name mismatch breaks the inheritance chain.
    ConcreteChild c;
    std::cout << c.f() << "\n";
    return 0;
}
