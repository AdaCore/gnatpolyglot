#include "test.h"

int main() {
    test::Extract e(0);

    e.set_b(99);

    test::ExtractLine l(0);

    l.set_a(42);

    l.print_vals(e);
}
