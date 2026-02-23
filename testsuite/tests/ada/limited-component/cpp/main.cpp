#include "test.h"
#include <iostream>
#include <type_traits>

static_assert(!std::is_trivially_constructible<test::Value>::value,
              "Value is constructible");

static_assert(!std::is_constructible<test::Value, test::Lim>::value,
              "Value is constructible with a test::Lim");

int main() {
    test::Value val = test::get_value();
    std::cout << val.get_v()->get_i() << "\n";
    test::HasDefault def;
    std::cout << def.get_def()->get_i() << "\n";
}
