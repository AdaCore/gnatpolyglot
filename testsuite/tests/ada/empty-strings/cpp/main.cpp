#include <iostream>

#include "gnatpolyglot_ada_strings.h"
#include "test.h"

int main() {
    gnatpolyglot::ada::strings::polyglot_string s = test::print_and_return("");
    std::cout << gnatpolyglot::ada::strings::to_string(s) << "\n";
}
