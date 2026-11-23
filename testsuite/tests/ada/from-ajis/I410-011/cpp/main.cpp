#include <iostream>
#include "vfs_child.h"

int main() {
    vfs::child::R r;
    std::cout << r.get() << "\n";
}
