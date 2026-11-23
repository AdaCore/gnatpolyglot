#include <iostream>
#include "test.h"

int main() {
    // Get_R returns Root'Class -> polyglot_ptr<test::Root>
    std::cout << test::get_r()->get_i() << "\n";
}
