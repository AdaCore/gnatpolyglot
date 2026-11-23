#include <iostream>

#include "definitions.h"
#include "users.h"

int main() {
    definitions::AType t0;
    definitions::AType t2 = definitions::create(2);
    if (t0 == definitions::get_null_value() && t2 == users::get_real_value()) {
        std::cout << "OK" << std::endl;
    }
    return 0;
}
