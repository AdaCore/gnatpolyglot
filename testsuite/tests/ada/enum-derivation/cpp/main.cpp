#include <algorithm>
#include <array>
#include <iostream>
#include <stdexcept>

#include "test.h"

std::string nameof(test::Enum1 val) {
    switch (val) {
        case test::Enum1::A:
            return "A";
        case test::Enum1::B:
            return "B";
        case test::Enum1::C:
            return "C";
        case test::Enum1::D:
            return "D";
        case test::Enum1::E:
            return "E";
        case test::Enum1::F:
            return "F";
    }
    throw std::invalid_argument("unknown value");
}

template<typename T, std::size_t S = 0>
void is_in(test::Enum1 val, const std::array<T, S> &values, const std::string& enum_name) {
    if (std::find(values.begin(), values.end(), static_cast<T>(val)) == values.end()) {
        std::cout << nameof(val) << " is not in " << enum_name << "\n";
    }
}

int main() {
    test::Enum1 e1 = test::f_enum(test::Enum1::A);
    std::cout << "C++ got: " << static_cast<int>(e1) << "\n";
    test::Enum2 e2 = test::f_enum(test::Enum2::B);
    std::cout << "C++ got: " << static_cast<int>(e2) << "\n\n";

    for (auto v : test::enum_1_values) {
        is_in(v, test::enum_2_values, "Enum2");
    }
    std::cout << "\n";

    for (auto v : test::enum_1_values) {
        is_in(v, test::enum_3_values, "Enum3");
    }
    std::cout << "\n";

    for (auto v : test::enum_1_values) {
        is_in(v, test::enum_4_values, "Enum4");
    }
}
