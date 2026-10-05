#include <iostream>
#include "pkg.h"

int main() {
    pkg::call_proc([](pkg::Enum &x) {
        std::cout << "C++: X = " << static_cast<int>(x) << std::endl;
        x = pkg::Enum::C;
    });
}
