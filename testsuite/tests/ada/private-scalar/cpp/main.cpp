#include <iostream>

#include "test.h"

int main() {
    test::T a = test::to_t(3);
    test::T b = test::to_t(4);
    a.print();
    b.print();

    a.add_t(b).print();
}
