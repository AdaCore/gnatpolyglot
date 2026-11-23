#include "hello.h"
#include "gnatpolyglot_ada_strings.h"
#include <iostream>

int main() {
    auto t = hello::get_v();
    std::cout << gnatpolyglot::ada::strings::to_string(t->get(t->get_begin()))
              << "\n";
}
