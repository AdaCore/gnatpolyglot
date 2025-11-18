#include <iostream>

#include "test.h"

class Child : public test::T {
public:
    Child() : test::T(this) {}

    test::Enum2 t_f(test::Enum1 e) const {
        switch (e) {
            case test::Enum1::A:
                return test::Enum2::D;
            case test::Enum1::B:
                return test::Enum2::E;
            default:
                return test::Enum2::F;
        };
    }
};

int main() {
   test::Enum1 e1 = test::Enum1::A;
   p_enum_1(e1);
   e1 = f_enum_1(e1);
   p_enum_1(e1);
   std::cout << "C++ value:" << static_cast<int>(e1) << "\n";

   test::Enum2 e2 = test::Enum2::E;
   p_enum_2(e2);
   e2 = f_enum_2(e2);
   p_enum_2(e2);
   std::cout << "C++ value:" << static_cast<int>(e2) << "\n";

   test::p_in_out(e2);

   std::cout << "C++ value:" << static_cast<int>(e2) << "\n";

   Child c;
   std::cout << static_cast<int>(test::call_t_f(c, test::Enum1::A)) << "\n";
   std::cout << static_cast<int>(test::call_t_f(c, test::Enum1::B)) << "\n";
   std::cout << static_cast<int>(test::call_t_f(c, test::Enum1::C)) << "\n";
}
