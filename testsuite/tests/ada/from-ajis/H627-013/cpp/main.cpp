#include <iostream>
#include <stdexcept>

#include "test.h"

class CppChild : public test::AType {
public:
    CppChild() : test::AType(this) {}
    int32_t raise_exception() const override {
        throw std::runtime_error("an exception...");
    }
};

int main() {
    CppChild child;
    test::AType &obj = child;

    try {
        test::call_and_raise(obj);
    } catch (...) {
        std::cout << "Exception got in java... " << std::endl;
    }

    try {
        test::call_and_catch(obj);
    } catch (...) {
        std::cout << "Exception got in java... " << std::endl;
    }
}
