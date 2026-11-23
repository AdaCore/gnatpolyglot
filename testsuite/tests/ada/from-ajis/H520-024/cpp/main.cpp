#include <cstdint>

#include "test.h"

int main() {
    // W is received as a const reference in C++: modify and return a copy.
    auto callback = [](std::int32_t /* v */, const test::ARecord &w) -> test::ARecord {
        test::ARecord res = w;
        res.set_v(res.get_v() + 1);

        return res;
    };

    test::call(callback);
}
