#include <iostream>

#include "test.h"

int main() {
    auto arr = test::foo();
    arr.get(1)();
    for (auto &it : arr) {
        it();
    }
    try {
        test::foo().set(1, nullptr);
    } catch (const std::exception &e) {
        std::cout << "caught exc: " << e.what() << "\n";
    }
    test::Rec{}.get_c()();
}
