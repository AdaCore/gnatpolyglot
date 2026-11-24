#include <iostream>

#include "test.h"

using gnatpolyglot::polyglot_ptr;

class C_Child : public test::C {
public:
    C_Child() : test::C(this) {
        std::cout << "CREATE JAVA C" << std::endl;
    }
};

int main() {
    test::C c;
    test::call(polyglot_ptr<test::C>(c));

    test::C2 c2(true);
    test::call2(polyglot_ptr<test::C2>(c2));

    C_Child c_child;
    test::call(polyglot_ptr<test::C>(c_child));
    return 0;
}
