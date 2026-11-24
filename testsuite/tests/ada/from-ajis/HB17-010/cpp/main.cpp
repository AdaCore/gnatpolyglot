#include <iostream>

#include "test.h"

namespace ps = gnatpolyglot::ada::strings;

class ACppChild : public test::ATaggedType {
public:
    ACppChild(int32_t a, int32_t b, const ps::polyglot_string &s1,
              const ps::polyglot_string &s2)
        : test::ATaggedType(a, b, s1, s2, this) {}

    void print_strings() const override {
        test::ATaggedType::print_strings();

        std::cout << "FROM CPP: [" << ps::to_string(*get_s1()) << "], ["
                  << ps::to_string(*get_s2()) << "]" << std::endl;
    }
};

int main() {
    test::ATaggedType v1(5, 2, "     ", "  ");
    v1.set_s1("12345");
    v1.set_s2("67");

    ACppChild v2(9, 1, "         ", " ");
    v2.set_s1("ABCDEFGHI");
    v2.set_s2("J");

    v1.print_strings();
    v2.print_strings();
    return 0;
}
