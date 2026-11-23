#include <iostream>
#include "test.h"

int main() {
    // Create an A_Type with F = 99
    test::AType v(99);

    // Set_X is not bound: anonymous access A_Type'Class parameter not yet supported.
    // X is not bound: A_Type_Access (access to classwide type) not yet supported.
    // The calls below show the desired behaviour once the feature is implemented.
    test::set_x(true, &v);
    std::cout << test::x()->get_f() << "\n";
}
