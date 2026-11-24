#include <iostream>
#include "test.h"

// C++ enums have no name conversion: print the Ada literal names by hand so
// that the output matches the AJIS one (Java enums' toString()).
static const char *name(test::Enum2 e) {
    static const char *names[] = {"A", "B", "C", "E", "F", "G"};
    return names[static_cast<int>(e)];
}

int main() {
    test::proc(test::Enum1::A);

    test::Enum2 ref = test::Enum2::B;
    test::proc_2(ref);
    std::cout << name(ref) << std::endl;

    std::cout << name(test::get()) << std::endl;

    test::call([](test::Enum2 v1, test::Enum2 &v2, test::Enum2 &v3) {
        std::cout << "V1, C++ callback: " << name(v1) << std::endl;
        std::cout << "V3, C++ callback: " << name(v3) << std::endl;

        v2 = test::Enum2::E;
        v3 = test::Enum2::F;
    });
    return 0;
}
