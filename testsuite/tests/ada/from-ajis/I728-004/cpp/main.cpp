#include <iostream>
#include "p.h"
namespace ps = gnatpolyglot::ada::strings;

int main() {
    p::ProjectTreeRef tree;
    std::cout << p::value_of(0, 0, tree) << "\n";
    std::cout << p::value_of_1(0, 0, tree) << "\n";
    std::cout << ps::to_string(p::value_of_2(0, 0, tree)) << "\n";
}
