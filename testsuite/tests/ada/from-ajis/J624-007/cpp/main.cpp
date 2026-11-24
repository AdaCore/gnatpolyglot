#include "test.h"

// Nothing is bound: P (anonymous access Boolean) not bound;
// R (field has anonymous access Integer) not bound;
// Acc (access to scalar Boolean) not bound; P2 (uses Acc) not bound.
// Original AJIS test also had an empty main.

int main() {
    return 0;
}
