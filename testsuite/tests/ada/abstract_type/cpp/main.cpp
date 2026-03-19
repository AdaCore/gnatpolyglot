#include <iostream>

#include "test.h"

class CppChild : public test::Root {
public:
    CppChild() : test::Root(this) {}

    void p2(int i) const override {
        this->p1();
        std::cout << "CppChild::p2 " << i << "\n";
    }
};

int main() {
    test::Child child(3);

    child.p1();
    child.p2(1);

    CppChild cpp_child;

    cpp_child.p1();
    cpp_child.p2(2);

    test::p2_root(cpp_child, 4);
}
