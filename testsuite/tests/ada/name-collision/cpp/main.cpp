#include "test.h"
#include "system.h"

int main() {
    test::void_();
    system_::Address address;
    test::delete_(address);
}
