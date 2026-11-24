#include <iostream>

#include "p_nested.h"

int main() {
    p::nested::set_v(987);
    std::cout << p::nested::get_v() << "\n";
}
