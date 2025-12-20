#include <iostream>

#include "test.h"

class Child : public test::T {
public:
    Child() : test::T(this) {}

    char t_f(char e) const {
        switch (e) {
            case 'a':
                return 'j';
            case 'b':
                return 'e';
            default:
                return 'f';
        };
    }
};

int main() {
   char e1 = 'A';
   test::p_character(e1);
   e1 = test::f_character(e1);
   test::p_character(e1);
   std::cout << "C++ value:" << e1 << "\n";

   char e2 = 'e';
   test::p_alphabet(e2);
   e2 = test::f_alphabet(e2);
   test::p_alphabet(e2);
   std::cout << "C++ value:" << e2 << "\n";

   test::p_in_out(e2);

   std::cout << "C++ value:" << e2 << "\n";

   Child c;
   std::cout << test::call_t_f(c, 'a') << "\n";
   std::cout << test::call_t_f(c, 'b') << "\n";
   std::cout << test::call_t_f(c, 'c') << "\n";
}
