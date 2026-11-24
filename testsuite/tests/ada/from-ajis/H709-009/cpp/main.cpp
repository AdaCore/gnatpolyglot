#include <iostream>
#include "test.h"

class Child : public test::T3 {
public:
    //  T_1.Val has no default value: no parameterless constructor is
    //  generated.
    Child(int32_t val) : test::T3(val, this) {}
};

int main() {
    Child child(1);
    test::T3 &v = child;

    v.set_val(999);

    //  AJIS renamed "F return Int_1" to F_Int_1 via pragma Annotate;
    //  GNATpolyglot binds that overload as f().
    std::cout << v.f() << "\n";
}
