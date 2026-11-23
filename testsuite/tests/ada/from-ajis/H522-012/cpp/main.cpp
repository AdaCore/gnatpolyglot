#include <iostream>
#include "test.h"
#include "test_child.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    std::cout << ps::to_string(*test::get()) << "\n";
    std::cout << ps::to_string(*test::child::get()) << "\n";
}
