#include <iostream>

#include "test.h"

int main() {
    auto s = test::return_string();
    std::cout << "STRING RECEIVED: " << gnatpolyglot::ada::strings::to_string(*s) << "\n";
}
