#include <iomanip>
#include <iostream>

#include "p1.h"

int main() {
    p1::Rec1 v;
    v.get_m()->set(1, 5, 99);

    std::cout << std::fixed << std::setprecision(1) << v.get_m()->get(1, 5)
              << std::endl;
    return 0;
}
