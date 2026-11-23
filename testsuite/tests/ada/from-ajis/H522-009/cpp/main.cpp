#include <iostream>

#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    ps::polyglot_string s("YO");
    auto r = test::f(gnatpolyglot::polyglot_ptr<ps::polyglot_string>(s));
    std::cout << ps::to_string(*r) << "\n";
}
