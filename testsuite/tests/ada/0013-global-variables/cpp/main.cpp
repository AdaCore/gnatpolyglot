#include <iostream>

#include "test.h"

int main() {
    std::cout << "Global_Int = " << test::get_global_int() << "\n";
    test::increment_int(test::get_global_int());
    std::cout << "Global_Int = " << test::get_global_int() << "\n";

    std::cout << "Global_Rec = " << test::get_global_rec()->get_i() << "\n";
    test::increment_rec(false, test::get_global_rec());
    std::cout << "Global_Rec = " << test::get_global_int() << "\n";

    std::cout << "Global_Arr = { ";
    for (int i = test::get_global_arr()->get_begin();
            i <= test::get_global_arr()->get_end(); i++)
        std::cout << test::get_global_arr()->get(i) << ", ";
    std::cout << "}\n";
    test::increment_arr(test::get_global_arr());
    std::cout << "Global_Arr = { ";
    for (int i = test::get_global_arr()->get_begin();
            i <= test::get_global_arr()->get_end(); i++)
        std::cout << test::get_global_arr()->get(i) << ", ";
    std::cout << "}\n";

    test::increment_int(test::get_global_b());
    std::cout << "Global_A = " << test::get_global_a() << "\n";
    std::cout << "Global_B = " << test::get_global_b() << "\n";

    std::cout << "Withed_Type = " << test::get_withed_type()->get_i() << "\n";
}
