#include <iostream>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    auto global_str = test::return_a_string_access();
    test::display_a_string_access(global_str);

    ps::polyglot_string other("This string is coming from Java");
    test::display_a_string_access(
        gnatpolyglot::polyglot_ptr<ps::polyglot_string>(other));
}
