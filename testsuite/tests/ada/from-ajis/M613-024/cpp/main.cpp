#include <iostream>
#include "hello.h"

namespace ps = gnatpolyglot::ada::strings;

int main() {
    // B has no default value in Ada, so no parameterless constructor is
    // generated: pass an arbitrary value (Say does not read it).
    std::cout << ps::to_string(hello::HelloRecord(false).say()) << "\n";
    return 0;
}
