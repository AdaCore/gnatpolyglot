#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include <iostream>

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::arrays;

int main() {
    {
        test::ARecord r;
        auto base = r.get_rec();
        base->set_v(102);
        std::cout << base->get_v() << "\n";
        base->set_v(103);
        std::cout << base->get_v() << "\n";
    }

    polyglot_ptr<test::AccessedByPointer> p(nullptr);
    {
        test::BaseRecord base;
        base.set_v(104);
        p = base.get_w();
    }
    // W designates an independent Ada allocation. Finalize does not free it.
    p->set_v(105);
    std::cout << p->get_v() << "\n";

    // Keep the array last so its broken destruction cannot hide the checks above.
    {
        polyglot_array<test::ARecord> arr(1, 1000);
        auto r = arr.get(5);
        r->get_rec()->set_v(99);
        std::cout << r->get_rec()->get_v() << "\n";
        r->get_rec()->set_v(100);
        std::cout << r->get_rec()->get_v() << "\n";

        // C++ component views borrow storage: retain arr while base is used.
        auto base = r->get_rec();
        std::cout << base->get_v() << "\n";
        base->set_v(101);
        std::cout << base->get_v() << "\n";
    }

    return 0;
}
