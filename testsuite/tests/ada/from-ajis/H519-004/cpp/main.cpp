#include <iostream>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    auto s = test::return_a_string();
    std::cout << ps::to_string(s) << "\n";
}
