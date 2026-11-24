#include <cstdint>

#include "test.h"

int main() {
    auto callback = [](std::int32_t a) -> std::int32_t { return a * 2; };

    test::call(callback, 10);
}
