#include <iostream>
#include <vector>
#include "test.h"

int main() {
    // F_1 and F_2 are not yet bound:
    //   F_1 returns A_Type'Class (class-wide return not yet supported)
    //   F_2 returns A_Type_Acc (access to classwide type not yet supported)
    // The calls below show the desired behaviour once the features are implemented.

    std::vector<test::AType> f1_objects;
    for (int j = 0; j < 10; ++j) {
        f1_objects.push_back(test::f_1());
    }

    for (int j = 0; j < 10; ++j) {
        test::f_2();
    }

    std::cout << "BEFORE GC\n";
    test::set_display_finalize(true);
    f1_objects.clear();
    std::cout << "AFTER GC\n";
    test::set_display_finalize(false);
}
