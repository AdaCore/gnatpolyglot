#include <iostream>

#include "gnatpolyglot_ada_arrays.h"
#include "test.h"

using namespace gnatpolyglot::ada::arrays;

int main() {
    std::cout << "LENGTH = " << test::get_v()->size() << "\n";

    // Java: a = (An_Array) Test_Package.V().clone();
    polyglot_array<int32_t> a = *test::get_v();

    std::cout << "LENGTH = " << a.size() << "\n";
    std::cout << "FIRST = " << a.get_begin() << "\n";
    std::cout << "LAST = " << a.get_end() << "\n";

    a = polyglot_array<int32_t>(1, 0);

    std::cout << "LENGTH = " << a.size() << "\n";
    std::cout << "FIRST = " << a.get_begin() << "\n";
    std::cout << "LAST = " << a.get_end() << "\n";

    return 0;
}
