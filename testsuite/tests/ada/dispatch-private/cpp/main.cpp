#include "test.h"

#include <iostream>

class Child : public test::Root {
public:
    Child() : test::Root(this) {}

    test::Priv f(const test::Priv &p) const override {
        std::cout << "Child::f\n";
        return p.inc();
    }
};

int main() {
    test::Root root;
    test::Priv priv;
    priv.print();
    test::call_f(root, priv).print();

    Child child;
    test::call_f(child, priv).print();
}
