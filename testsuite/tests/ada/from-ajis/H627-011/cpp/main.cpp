#include <iostream>

#include "test.h"

using gnatpolyglot::polyglot_ptr;
namespace ps = gnatpolyglot::ada::strings;

int main() {
    test::AType t(polyglot_ptr<ps::polyglot_string>(new ps::polyglot_string("YO"),
                                                    gnatpolyglot::memory_owner::LIBRARY),
                  3, "   ");
    t.set_loc_str("123");
    std::cout << ps::to_string(*t.get_loc_str()) << std::endl;
    return 0;
}
