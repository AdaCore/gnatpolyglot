#include "test.h"
#include "gnatpolyglot_ptr.h"
#include "gnatpolyglot_ada_arrays.h"
#include <iostream>

int main() {
    // SmallRec has no default constructor: SmallRec(Enum f1, Enum f2)
    test::SmallRec d_val(test::Enum::E1, test::Enum::E1);
    test::SmallRec e_small(test::Enum::E3, test::Enum::E1);
    gnatpolyglot::polyglot_ptr<test::SmallRec> e_ptr(e_small);
    test::Rec r(false, false, test::Enum::E1, d_val, e_ptr);
    std::cout << "r.D.F1 = " << static_cast<int>(r.get_d()->get_f1()) << "\n";
    std::cout << "r.E.F1 = " << static_cast<int>(r.get_e()->get_f1()) << "\n";

    gnatpolyglot::ada::arrays::polyglot_array<test::SmallRec> arr(1, 3);
    arr.get(1)->set_f2(test::Enum::E1);
    arr.get(2)->set_f2(test::Enum::E2);
    arr.get(3)->set_f2(test::Enum::E3);
    for (int i = 1; i <= 3; ++i) {
        std::cout << "arr [" << i << "] = "
                  << static_cast<int>(arr.get(i)->get_f2()) << "\n";
    }

    test::get_global_var()->set_f1(test::Enum::E1);
    test::get_global_var()->set_f2(test::Enum::E2);
    std::cout << "Global_Var.F1 = "
              << static_cast<int>(test::get_global_var()->get_f1()) << "\n";
    std::cout << "Global_Var.F2 = "
              << static_cast<int>(test::get_global_var()->get_f2()) << "\n";
    return 0;
}
