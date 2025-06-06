#include <iostream>

#include "test.h"

int main() {
    int a = -1;
    test::T b;
    std::cout << "Calling in_proc\n";
    test::in_proc(a, b);
    std::cout << "Calling out_proc\n";
    test::out_proc(a, b);
    std::cout << "Calling in_proc\n";
    test::in_proc(a, b);
    std::cout << "Calling in_out_proc\n";
    test::in_out_proc(a, b);
    std::cout << "Calling in_proc\n";
    test::in_proc(a, b);
}
