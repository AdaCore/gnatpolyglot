#include <iostream>

#include "test.h"

class Child : public test::Root {
public:
    Child(int i) : test::Root(this), i(i) {}

    void p1() override {
        i += 1;
        std::cout << "i = " << i << "\n";
    }

    int i;
};

int main() {
    test::Root root;
    root.p1();
    std::cout << "\n";

    Child c(3);
    c.p1();
    std::cout << "\n";
}
