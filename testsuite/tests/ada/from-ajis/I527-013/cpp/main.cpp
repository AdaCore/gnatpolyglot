#include "p.h"
#include "base.h"
#include <iostream>

int main() {
    p::call([](base::T& x) {
        std::cout << x.get_v() << "\n";
    });
    return 0;
}
