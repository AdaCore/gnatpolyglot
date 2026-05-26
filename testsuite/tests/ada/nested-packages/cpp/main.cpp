#include "test.h"
#include "test_inner.h"

int main() {
    test::outer_proc();
    test::inner::inner_proc();
}
