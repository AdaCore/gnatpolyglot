#include <iostream>
#include <typeinfo>
#include "test.h"

// Transposition of Java's getClass(): the exact dynamic C++ class.
static const char *class_of(const test::Root &v) {
    if (typeid(v) == typeid(test::Child1))
        return "Child1";
    else if (typeid(v) == typeid(test::Root))
        return "Root";
    else
        return typeid(v).name();
}

int main() {
    auto v1 = test::get_object(1);
    auto v2 = test::get_object(2);
    auto v3 = test::get_object(3);
    auto v4 = test::get_object(4);
    auto v5 = test::get_object(5);

    std::cout << "ROOT: " << "Root" << "\n";
    std::cout << "CHILD 1: " << "Child1" << "\n";

    std::cout << class_of(*v1) << "\n";
    std::cout << class_of(*v2) << "\n";
    std::cout << class_of(*v3) << "\n";
    std::cout << class_of(*v4) << "\n";
    std::cout << class_of(*v5) << "\n";
}
