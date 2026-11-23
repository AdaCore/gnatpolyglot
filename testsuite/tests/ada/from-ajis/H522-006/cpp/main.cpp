#include <iostream>
#include "test.h"

class CppChild : public test::Root {
public:
    CppChild() : test::Root(this) {}

    void p_1() const override {
        std::cout << "P_1 of CppChild\n";
    }
};

int main() {
    test::Child1 v1;
    test::Root& v2 = test::get_direct_root_child();  // not bound: class-wide return not yet supported
    CppChild v3;

    v1.p_1();
    v2.p_1();
    v3.p_1();

    test::display_p1(true, v1);
    test::display_p1(true, v2);
    test::display_p1(true, v3);
}
