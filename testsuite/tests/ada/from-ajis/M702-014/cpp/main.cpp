#include <iostream>

#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    std::cout << ps::to_string(test::f());

    std::cout.flush();
}
