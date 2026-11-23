#include <iostream>
#include <stdexcept>

#include "test.h"

class Inner : public test::RootType {
public:
    Inner() : test::RootType(this) {}

    test::SomeType primitive() override {
        throw std::runtime_error("cannot implement this...");
    }
};

int main() {
    Inner inner;
    test::RootType &v = inner;

    try {
        v.primitive();
    } catch (const std::exception &e) {
        std::cout << "CORRECT PRIMITIVE CALLED" << std::endl;
    }
    return 0;
}
