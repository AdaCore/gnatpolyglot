#include <iostream>

#include "../2cpp/include/example.h"
#include "gnatpolyglot_ada_strings.h"

int main() {
    example::Counter c{};

    c.increment();
    std::cout << "count = " << c.get_count() << "\n";
    c.increment(3);
    std::cout << "count = " << c.get_count() << "\n";
    c.reset();
    std::cout << "count = " << c.get_count() << "\n";
    c.get_count() = 5;
    std::cout << "count = " << c.get_count() << "\n";
    c.set_count(6);
    std::cout << "count = " << c.get_count() << "\n";
}
