#include <cassert>
#include <climits>
#include <iostream>

#include "ints.h"

template <typename S, typename T>
void minimum_size(T) {
    assert(sizeof(S) <= sizeof(T));
}

int main() {
    std::cout << std::hex;
    assert(ints::f_char() == 'P');
    minimum_size<char>(ints::f_char());
    assert(ints::f_short() == SHRT_MAX);
    minimum_size<short>(ints::f_short());
    assert(ints::f_int() == INT_MAX);
    minimum_size<int>(ints::f_int());
    assert(ints::f_long_int() == LONG_MAX);
    minimum_size<long>(ints::f_long_int());

    std::cout << std::dec;
    assert(ints::f_my_int() == (1 << 20));
    assert(ints::f_my_new_short() == 1000);
    assert(ints::f_my_long_int() == LONG_MIN);
    assert(ints::f_my_small() == -256);

    assert(ints::f_positive() == 1);
    assert(ints::f_my_positive() == 2);
    assert(typeid(ints::f_my_positive()) == typeid(unsigned)
            || typeid(ints::f_my_positive()) == typeid(unsigned long));
}
