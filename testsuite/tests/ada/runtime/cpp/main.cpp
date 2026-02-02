#include "polyglot_ada_arrays.h"
#include "polyglot_ada_strings.h"
#include "test.h"

int main() {
    ada::calendar::Time time = test::get_time();
    ada::strings::unbounded::UnboundedString s;
    test::append(s, time);
    std::cout << polyglot::ada::strings::to_string(test::to_string(s)) << "\n";

    polyglot::ada::arrays::polyglot_array<ada::calendar::Time> arr(1, 3);
    for (int i = 1; i <= 3; i++)
        arr.set(i, time);
    test::append_arr(s, arr);
    std::cout << polyglot::ada::strings::to_string(test::to_string(s)) << "\n";
}
