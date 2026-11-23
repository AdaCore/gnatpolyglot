#include <cstdint>
#include <iostream>

#include "test.h"

int main() {
    auto p = [](int32_t &x, char &y, test::Enum &z, bool &t) {
        // AJIS was run with --no-java-enum and its CharacterRef prints the
        // character code: print Y and Z as integers as well.
        std::cout << "From C++, {X, Y, Z, T}={" << x << ", "
                  << static_cast<int>(y) << ", " << static_cast<int>(z)
                  << ", " << std::boolalpha << t << "}" << std::endl;

        x = 42;
        y = 'z';
        z = test::Enum::C;
        t = false;
    };

    test::call_proc(p);
}
