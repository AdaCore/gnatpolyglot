#include "iterable_containers.h"
#include <iostream>

int main() {
    iterable_containers::IntegerContainer container;
    // Preserve the host-language iteration check. Iterable is not mapped yet.
    for (int i : *container.iterate()) {
        std::cout << i << " Integer_Container\n";
    }
}
