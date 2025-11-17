#include <cmath>
#include <cassert>
#include <iomanip>
#include <ios>
#include <iostream>
#include <limits>

#include "floats.h"

template <typename S, typename T>
void minimum_size(T) {
    assert(sizeof(S) <= sizeof(T));
}

template<typename T>
void p(T ld){
    std::cout << "got: " << ld << "\n";
}

int main() {
    std::cout.precision(7);

    minimum_size<float>(floats::f_short());
    p(floats::f_short());

    minimum_size<float>(floats::f_float());
    p(floats::f_float());

    minimum_size<double>(floats::f_long_float());
    p(floats::f_long_float());

    minimum_size<long double>(floats::f_long_long_float());
    p(floats::f_long_long_float());

    minimum_size<float>(floats::f_my_float());
    p(floats::f_my_float());

    minimum_size<float>(floats::f_my_new_short());
    p(floats::f_my_new_short());

    minimum_size<double>(floats::f_my_long_float());
    p(floats::f_my_long_float());

    minimum_size<float>(floats::f_my_small());
    p(floats::f_my_float());

    p(floats::inc(4.5));

    p(floats::identity(-0.0));
    p(floats::identity(std::numeric_limits<float>::infinity()));
    p(floats::identity(std::numeric_limits<float>::quiet_NaN()));
}
