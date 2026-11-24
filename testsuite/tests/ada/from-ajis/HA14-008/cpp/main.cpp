#include <iostream>
#include "test.h"

using gnatpolyglot::ada::strings::polyglot_string;
using gnatpolyglot::ada::strings::to_string;

class CppRoot : public test::Root {
public:
    CppRoot() : test::Root(this) {}

    void prim(const test::Container & /*obj*/) const override {
        std::cout << "PRIM CALLED FROM C++" << std::endl;
    }

    void prim2(const polyglot_string &str) const override {
        std::cout << "PRIM2 CALLED FROM C++: " << to_string(str) << std::endl;
    }
};

int main() {
    test::Contained x;
    test::Container c(x);
    CppRoot r;

    test::call_prim(r, c);
    test::call_prim2(r);

    return 0;
}
