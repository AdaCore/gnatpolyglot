#include <iostream>

#include "test.h"

class CppChild1 : public test::Root1 {
public:
    CppChild1() : test::Root1(this) {}
    void p() const override { std::cout << "P of C1\n"; }
};

class CppChild2 : public test::Root2 {
public:
    CppChild2() : test::Root2(this) {}
    void p() const override { std::cout << "P of C2\n"; }
};

int main() {
    test::Root1 r1;
    r1.p();
    CppChild1 c1;
    c1.p();
    CppChild2 c2;
    c2.p();
}
