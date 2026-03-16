#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::strings;

int main() {
    test::T t("aaaaaaaaaa");
    std::cout << to_string(t.get_s()) << "\n";
    t.set_s("bbbbbbbbbb");
    t.print();
}
