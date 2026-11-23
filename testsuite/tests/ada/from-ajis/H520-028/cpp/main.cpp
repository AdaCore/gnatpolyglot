#include <iostream>
#include "test.h"

int main() {
    std::cout << "F_Integer = " << test::standard_integer_f() << "\n";
    std::cout << "F_Record = " << test::test_r_f().get_v() << "\n";
}
