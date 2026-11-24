#include <iostream>

#include "test.h"

int main() {
    if (!test::create_string(true)) {
        std::cout << "STRING GOT IS NULL" << std::endl;
    } else {
        std::cout << "STRING GOT IS NOT NULL" << std::endl;
    }

    std::cout << "PRINTING A NULL STRING ACCESS AS A STRING ACCESS" << std::endl;
    test::print_string_access(nullptr);

    //  This is not supposed to work, since we don't expect a pointer
    //  here...
    //  std::cout << "PRINTING A NULL STRING ACCESS AS A STRING" << std::endl;
    //  test::print_string(nullptr);

    return 0;
}
