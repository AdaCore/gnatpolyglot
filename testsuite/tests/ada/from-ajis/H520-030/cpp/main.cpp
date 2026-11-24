#include <iostream>

#include "test.h"

int main() {
    test::ARecord r(10);

    r.initialize();

    std::cout << "RESULT = " << r.get_sum() << std::endl;
    return 0;
}
