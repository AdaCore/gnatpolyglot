#include "test.h"
#include <iostream>

// Prim, P2: anonymous access A_Tagged_Type parameter not bound.
// Call_Prim: access A_Tagged_Type'Class (classwide) not bound.
// Sth: global of access A_Tagged_Type type.

class Child : public test::ATaggedType {
public:
    Child() : test::ATaggedType(this) {}
};

int main() {
    auto v1 = test::sth();
    auto v2 = test::prim(v1);
    std::cout << v1->get_v() << "\n";
    v2->set_v(999);
    std::cout << v1->get_v() << "\n";
    std::cout << v2->get_v() << "\n";

    Child c;
    auto v3 = test::call_prim(&c);
    std::cout << c.get_v() << "\n";
    v3->set_v(998);
    std::cout << c.get_v() << "\n";
    std::cout << v3->get_v() << "\n";
    return 0;
}
