#include <iostream>

#include "t.h"

int main() {
    t::Rec r(1234, 0, 0, 0);
    std::cout << "The discriminant of Rec r is " << r.get_disc() << std::endl;
    return 0;
}
