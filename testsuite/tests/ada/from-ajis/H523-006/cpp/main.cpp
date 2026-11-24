#include <iostream>

#include "test.h"

int main() {
    test::AType v;
    std::cout << v.get() << "\n";  // not bound: anonymous access type parameter
}
