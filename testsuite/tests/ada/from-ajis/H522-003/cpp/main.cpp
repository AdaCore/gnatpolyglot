#include <iostream>
#include "test.h"

class CppChild : public test::Rec {
public:
    CppChild() : test::Rec(this) {}

    void p_1() const override {
        std::cout << "P_1 of CppChild\n";
    }
};

int main() {
    test::Rec v1;
    CppChild v2;
    v1.p_1();
    v2.p_1();
    test::call_p1(true, v1);
    test::call_p1(true, v2);
}
