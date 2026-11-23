#include <iostream>

#include "hello.h"

int main() {
    hello::GeneralEntityReference ref;
    ref.for_each_dispatching_call(nullptr);
    std::cout << "OK" << std::endl;
}
