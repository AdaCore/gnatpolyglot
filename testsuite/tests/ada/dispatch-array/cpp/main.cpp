#include "test.h"
#include "gnatpolyglot_ada_arrays.h"

#include <iostream>

void print(const gnatpolyglot::ada::arrays::polyglot_array<int> &a) {
    for (int i = a.get_begin(); i <= a.get_end(); i++)
       std::cout << a.get(i) << ", ";
    std::cout << "\n";
}

class Child : public test::Root {
public:
    Child() : test::Root(this) {}

    gnatpolyglot::ada::arrays::polyglot_array<int>
    f(gnatpolyglot::ada::arrays::polyglot_array<int> &a) const override {
        std::cout << "Child::f\n";
        gnatpolyglot::ada::arrays::polyglot_array<int> res = a;
        for (int i = a.get_begin(); i <= a.get_end(); i++)
           a.set(i, a.get(i) + 2);

        return res;
    }
};

int main() {
    test::Root root;
    gnatpolyglot::ada::arrays::polyglot_array<int> a(1, 10);
    for (int i = a.get_begin(); i <= a.get_end(); i++)
       a.set(i, i);
    print(a);
    print(test::call_f(root, a));
    print(a);

    Child child;
    print(test::call_f(child, a));
    print(a);
}
