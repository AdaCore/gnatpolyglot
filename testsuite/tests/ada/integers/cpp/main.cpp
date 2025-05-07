#include <cstdint>
#include <iostream>

#include "ints.h"

void p(uint8_t c) {
    std::cout << "print char: " << (char) c << "\n";
}

void p(short s){
    std::cout << "print short: " << s << "\n";
}

void p(int i){
    std::cout << "print int: " << i << "\n";
}

void p(long l){
    std::cout << "print long: " << l << "\n";
}

void p(unsigned int l){
    std::cout << "print unsigned int: " << l << "\n";
}

int main() {
    std::cout << std::hex;
    p(ints::f_char());
    p(ints::f_short());
    p(ints::f_int());
    p(ints::f_long_int());

    std::cout << std::dec;
    p(ints::f_my_int());
    p(ints::f_my_new_short());
    p(ints::f_my_long_int());
    p(ints::f_my_small());

    p(ints::f_positive());
    p(ints::f_my_positive());
}
