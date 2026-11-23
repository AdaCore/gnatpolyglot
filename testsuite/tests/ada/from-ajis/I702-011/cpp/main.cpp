#include "p.h"
#include <iostream>

int main() {
    p::StructuredFileAccess sfa;
    auto lock = p::lock_updates(sfa);
    lock.unlock();
    return 0;
}
