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

   // An enumeration whose representation does not fit in 32 bits must use a
   // 64-bit underlying type: the literal value is preserved, not truncated to
   // the low 32 bits (which would print 705032704).
   std::cout << "C++ big:" << static_cast<long long>(test::EnumBig::I) << "\n";
   // In-range items of the same enum still round-trip through the binding.
   test::EnumBig eb = f_enum_big(test::EnumBig::G);
   p_enum_big(eb);

   test::Enum3 e3 = test::Enum3::A;
   p_enum_1(e3);

   std::cout << static_cast<int>(c.get_e()) << "\n";
   c.set_e(test::Enum1::B);
   std::cout << static_cast<int>(c.get_e()) << "\n";
   // An enum getter returns a real reference too: writing through it mutates in place.
   c.get_e() = test::Enum1::C;
   std::cout << static_cast<int>(c.get_e()) << "\n";
}
