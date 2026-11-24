#include "hello.h"
#include <iostream>

int main() {
    hello::GE concrete;
    hello::RE &e = concrete;
    if (e.pgi() == 1961) {
        std::cout << "OK\n";
    }
}
