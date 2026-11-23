#include <iostream>

#include "p.h"

int main() {
    p::Child13 c;

    c.set_f(999);

    std::cout << c.get_f() << std::endl;
    return 0;
}
