#include <iostream>

#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    auto my_proc = [](const ps::polyglot_string &text) {
        std::cout << ps::to_string(text) << "\n";
    };

    test::do_something(my_proc);
}
