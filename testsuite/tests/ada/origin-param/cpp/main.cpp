#include <iostream>

#include "test.h"
#include "test_child.h"

int main() {
    // Ensure that test::NotInt is not binded as an int
    test::NotInt i = test::get();
    test::child::f(i);
    test::child::Rec r{i};
    i = r.get_i();
    r.set_i(test::child::get_g());
}
