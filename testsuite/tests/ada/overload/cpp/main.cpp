#include <iostream>

#include "test.h"

int main() {
    test::T1 t;

    test::overloaded_proc();
    test::overloaded_proc(1);

    std::cout << "Got from ada :" << test::overloaded_fun(1, 2, 3) << "\n"
              << "Got from ada :" << test::overloaded_fun(4, t, t) << "\n";

    test::void_overloaded_ret();
    std::cout << "Got from ada :" << test::standard_integer_overloaded_ret() << "\n"
              << "Got from ada :" << test::test_t1_overloaded_ret().get_v() << "\n"
              << "Got from ada :" << test::standard_integer_overloaded_ret(4) << "\n";

    // Functions with different return type and arguments should not be renamed
    std::cout << "10 * 2 = " << test::no_rename(10) << "\n"
              << "10L * 4 = " << test::no_rename(10L) << "\n";

    // Overlaoded_Int should have mapped to two differently named functions.
    test::overloaded_int(1);
    test::overloaded_int_1(2);
}
