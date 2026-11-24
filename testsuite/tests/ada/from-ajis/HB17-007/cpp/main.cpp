#include <cstdint>
#include <iomanip>
#include <iostream>
#include "test.h"

int main() {
    test::call_a_callback([](int64_t &v1, int64_t &v2, float &v3, float &v4,
                             bool &v5, bool &v6, test::AnEnum &v7,
                             test::AnEnum &v8, char &v9, char &v10) {
        // AJIS was run with --no-java-enum: enums are seen as their
        // position number.
        std::cout << v2 << ", " << std::fixed << std::setprecision(1) << v4
                  << ", " << std::boolalpha << v6 << ", "
                  << static_cast<int>(v8) << ", " << v10 << std::endl;

        v1 = 987;
        v2 = 654;
        v3 = 40.0f;
        v4 = 42.0f;
        v5 = false;
        v6 = true;
        v7 = test::AnEnum::A;
        v8 = test::AnEnum::C;
        v9 = 'w';
        v10 = 'e';
    });
    return 0;
}
