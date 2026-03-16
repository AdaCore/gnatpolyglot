#include <iostream>

#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "test.h"

int main() {
    ada::calendar::Time time = test::get_time();
    ada::strings::unbounded::UnboundedString s;
    test::append(s, time);
    std::cout << gnatpolyglot::ada::strings::to_string(test::to_string(s)) << "\n";

    gnatpolyglot::ada::arrays::polyglot_array<ada::calendar::Time> arr(1, 3);
    for (int i = 1; i <= 3; i++)
        arr.set(i, time);
    test::append_arr(s, arr);
    std::cout << gnatpolyglot::ada::strings::to_string(test::to_string(s)) << "\n";
}
