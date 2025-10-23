#include <iostream>

#include "test.h"

class CppType : public test::T1 {
public:
    CppType() : test::T1(this) {}

    void overloaded_proc() const override {
        std::cout << "Hello from c++" << "\n";
    }

    void overloaded_proc(int a) const override {
        std::cout << "Hello from c++: " << a << "\n";
    }
};

int main() {
    test::T1 t;
    CppType cpp;

    test::call_overload(t);
    test::call_overload(t, 1);
    test::call_overload(cpp);
    test::call_overload(cpp, 2);
}
