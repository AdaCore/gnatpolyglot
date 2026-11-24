#include <iostream>

#include "test.h"

class Child : public test::Bla {
public:
    Child(int32_t v) : test::Bla(v, this) {}
};

int main() {
    test::Bla x(5);
    Child y(5);
    std::cout << "Object creation worked" << std::endl;
    return 0;
}
