#include <iostream>

#include "test.h"

int main() {
    test::T1 t;

    test::overloaded_proc();
    test::overloaded_proc(1);

    std::cout << "Got from ada :" << test::overloaded_fun(1, 2, 3) << "\n"
              << "Got from ada :" << test::overloaded_fun(4, t, t) << "\n";
}
