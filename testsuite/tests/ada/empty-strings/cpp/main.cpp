#include <iostream>

#include "polyglot_ada_strings.h"
#include "test.h"

int main() {
    polyglot::ada::strings::polyglot_string s = test::print_and_return("");
    std::cout << polyglot::ada::strings::to_string(s) << "\n";
}
