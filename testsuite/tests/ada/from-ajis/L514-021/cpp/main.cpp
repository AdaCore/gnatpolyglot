#include <iostream>

#include "base.h"
#include "ren.h"

// C++ enums have no image: print the Ada literal name, as Java's
// Enum.toString() does.
static const char *image(base::Enum e) {
    static const char *names[] = {"B", "C", "D"};
    return names[static_cast<int>(e)];
}

int main() {
    std::cout << ren::get_a() << "\n";
    std::cout << image(ren::get_e()) << "\n";
    return 0;
}
