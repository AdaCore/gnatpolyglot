#include "test.h"
#include "test_system.h"
#include "system.h"

int main() {
    test::void_();
    system_::Address address;
    test::delete_(address);

    test::system::Foo f;
    test::foo(address);
}
