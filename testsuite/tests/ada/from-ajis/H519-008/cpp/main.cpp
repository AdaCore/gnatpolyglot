#include <iostream>
#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    auto v2_init = test::get_v2();
    std::cout << test::get_v1() << ", " << ps::to_string(*v2_init) << "\n";

    test::set_v1(999);
    ps::polyglot_string s2("9876543210");
    test::set_v2(s2);
    auto v2_new = test::get_v2();
    std::cout << test::get_v1() << ", " << ps::to_string(*v2_new) << "\n";

    ps::polyglot_string s3("This is the rythm of the life, yeah !");
    test::set_v3(gnatpolyglot::polyglot_ptr<ps::polyglot_string>(s3));
    auto v3 = test::get_v3();
    std::cout << ps::to_string(*v3) << "\n";
}
