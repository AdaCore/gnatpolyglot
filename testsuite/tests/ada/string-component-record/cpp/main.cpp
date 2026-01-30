#include "polyglot_ada_strings.h"
#include "polyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace polyglot;
using namespace polyglot::ada::strings;

int main() {
    test::T t("aaaaaaaaaa");
    std::cout << to_string(t.get_s()) << "\n";
    t.set_s("bbbbbbbbbb");
    t.print();
}
