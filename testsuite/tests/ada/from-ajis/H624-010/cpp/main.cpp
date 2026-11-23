#include <iostream>
#include "test.h"

int main() {
    // test::PAcc, test::get_null_p_acc, test::call_p_acc are not generated
    // because P_Acc (access to subprogram) is not yet supported
    auto v = test::get_null_p_acc();
    if (v == nullptr) {
        std::cout << "V is null\n";
    }
    test::call_p_acc(nullptr);
    test::call_p_acc([]() { std::cout << "P_Acc from C++...\n"; });
}
