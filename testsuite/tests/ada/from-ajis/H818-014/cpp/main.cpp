#include "test.h"
#include <iostream>

// Type1 can be subclassed (has shadow constructor and virtual some_primitive)
class CppType1 : public test::Type1 {
public:
    CppType1() : test::Type1(this) {}

    void some_primitive() const override {
        std::cout << "some_primitive called\n";
    }
};

// Type2 cannot be properly subclassed: Get is not bound (class-wide return
// not yet supported), and Type2 has no shadow constructor.
// create_and_free dispatches to V.Get internally in Ada, but without a
// virtual get() binding, a C++ subclass cannot override it.
class CppType2 : public test::Type2 {
public:
    // No shadow constructor available for Type2
};

int main() {
    CppType2 t2;
    // create_and_free internally calls V.Get (abstract) and some_primitive.
    // Since get() is not a virtual method in C++ (not bound), Ada dispatch
    // to a C++ override is not possible.
    test::create_and_free(t2);

    return 0;
}
