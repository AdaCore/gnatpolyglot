#include <iostream>

#include "test.h"

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
}
