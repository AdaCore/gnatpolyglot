#include <iostream>

#include "test.h"

int main() {
    std::cout << std::boolalpha;

    auto r1 = test::get_new_r(9);
    auto r2 = test::get_new_r(9);
    auto r3 = test::get_new_r(5);
    std::cout << "EXPECTED EQUALS " << (r1.get_v() == r2.get_v()) << "\n";
    std::cout << "EXPECTED NOT EQUALS " << (r1.get_v() == r3.get_v()) << "\n";

    auto rt1 = test::get_new_r_tagged(19);  // not bound: class-wide return not yet supported
    auto rt2 = test::get_new_r_tagged(19);
    auto rt3 = test::get_new_r_tagged(15);
    std::cout << "EXPECTED EQUALS " << (rt1.get_v() == rt2.get_v()) << "\n";
    std::cout << "EXPECTED NOT EQUALS " << (rt1.get_v() == rt3.get_v()) << "\n";
    std::cout << "EXPECTED NOT EQUALS, BUT WITHOUT EXCEPTION " << false << "\n";
}
