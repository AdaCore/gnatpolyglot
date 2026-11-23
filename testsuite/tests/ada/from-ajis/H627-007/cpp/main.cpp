#include "test.h"

int main() {
    test::A v1;
    test::B v2;
    v1 = v1.increment_all();
    v2 = v2.increment_all();
    v1.print();
    v2.print();
}
