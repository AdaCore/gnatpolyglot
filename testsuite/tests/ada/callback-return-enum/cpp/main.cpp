#include <cstdint>
#include <iostream>
#include "pkg.h"

int main() {
    pkg::call_func([](int32_t n) {
        return n > 0 ? pkg::Enum::C : pkg::Enum::A;
    });
}
