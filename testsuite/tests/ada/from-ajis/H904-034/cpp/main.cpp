#include <stdexcept>

#include "test.h"

int main() {
    test::call([] { throw std::runtime_error("callback failed"); });
}
