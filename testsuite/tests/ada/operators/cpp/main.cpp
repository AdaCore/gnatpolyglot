#include <iostream>

#include "test.h"

using namespace test;

void print(const test::Pair &p) {
    std::cout << "{" << p.get_left() << ", " << p.get_right() << "}\n";
}

int main() {
    print(Pair(1, 2) + Pair(3, 4));
    std::cout << +Pair(1, 3) << "\n";
    print(Pair(3, 4) - Pair(1, 2));
    std::cout << -Pair(1, 3) << "\n";
    print(Pair(2, 3) * Pair(4, 5));
    print(Pair(20, 15) / Pair(4, 5));
    print(Pair(2, 2).operator_pow(Pair(4, 5)));
    print(Pair(20, 15) % Pair(4, 5));
    print(Pair(20, 15).operator_rem(Pair(4, 5)));
    print(Pair(-15, 15).operator_abs());
    print(Pair(20, 15).operator_concat(Pair(4, 5)));
    std::cout << (Pair(1, 2) == Pair(1, 2)) << "\n";
    std::cout << (Pair(1, 3) == Pair(1, 2)) << "\n";
    std::cout << (Pair(1, 2) != Pair(1, 2)) << "\n";
    std::cout << (Pair(1, 3) != Pair(1, 2)) << "\n";
    print(Pair(1, 2) & Pair(3, 4));
    print(Pair(1, 2) | Pair(3, 4));
    print(Pair(-1, -1) ^ Pair(1, 2));
    print(~Pair(~3, ~4));
    print(Pair(10, 10) != 2);

    std::cout
       << gnatpolyglot::ada::strings::to_string(test::operator_mult(4, 'c'))
       << "\n";

    std::cout
       << gnatpolyglot::ada::strings::to_string(+"foo")
       << "\n";

}
