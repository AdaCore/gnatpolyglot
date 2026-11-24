#include <iostream>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    test::Root r;
    auto s = ps::from_string("          ");

    r.fill(s);
    std::cout << ps::to_string(s) << "\n";

    r.set_filling('z');
    r.fill(s);
    std::cout << ps::to_string(s) << "\n";

    return 0;
}
