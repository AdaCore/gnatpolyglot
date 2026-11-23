#include <iostream>

#include "p.h"

int main() {
    p::set_v(2001);
    std::cout << p::get_v() << "\n";
}
