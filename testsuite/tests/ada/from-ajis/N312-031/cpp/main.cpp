#include <iostream>

#include "hello.h"

int main() {
    if (hello::get_o_class()->get_i() == 1961) {
        std::cout << "OK" << std::endl;
    }
}
