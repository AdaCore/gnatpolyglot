#include <iostream>

#include "p.h"

int main() {
    p::get_v2()->set(1, 987);
    std::cout << p::get_v2()->get(1) << "\n";
}
