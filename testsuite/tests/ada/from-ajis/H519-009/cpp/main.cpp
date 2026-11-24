#include <iostream>
#include <iomanip>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    ps::polyglot_string s("Kisses from Java");
    test::ARecord obj(999, 118.0f,
                      gnatpolyglot::polyglot_ptr<ps::polyglot_string>(s));
    std::cout << obj.get_a() << ", "
              << std::fixed << std::setprecision(1) << obj.get_b() << ", "
              << ps::to_string(*obj.get_c()) << "\n";
}
