#include "test.h"
#include "test_priv.h"
#include "test_publ.h"

int main() {
    test::Root root(1, 2);
    test::Rec recr(1, 2);

    root.p1();

    test::Child child(3, 4, 5);
    test::Rec recc(3, 4);

    child.p1();

    root.p2(recr);
    child.p2(recc);

    test::OtherChild other_child(3, 4, 5);
    other_child.p1();

    test::Root &foo = other_child;
    foo.p1();

    test::p_root(root);
    test::p_root(child);
    test::p_root(other_child);

    test::priv::PrivRoot priv;
    priv.foo();

    test::publ::Publ publ(1, 3);
    publ.foo();
}
