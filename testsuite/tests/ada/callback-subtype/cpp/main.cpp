#include <iostream>

#include "test.h"

int main() {
    std::cout << test::call([](int i){ return i + 1;}) << '\n';
    std::cout << test::get_callback()(4) << '\n';
}
