#include <iostream>
#include <string>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    test::R1 v1(0);
    test::R2 v2(0);

    v1.set_v(99);
    v2.set_v(88);

    test::print
      (test::get_std_output(),
       ps::from_string(std::to_string(v1.get_v()) + ", "
                       + std::to_string(v2.get_v())));
}
