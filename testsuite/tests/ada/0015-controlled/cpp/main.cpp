#include "polyglot_ada_arrays.h"
#include "test.h"

class InheritsControlled : public test::Cont { };

int main() {
    {
        test::Cont cont;
        test::Cont cont2;
        cont = cont2;
        test::Cont foo{cont};
        cont.p();
        cont2.p();
        foo.p();
    }
    std::cout << std::endl;
    {
        InheritsControlled cont;
        cont.p();
    }
    std::cout << std::endl;
    {
        test::Cont cont;
        test::ContWrapper wrapper(cont);
        test::ContWrapper cont2;
        cont.p();
        wrapper.get_c()->p();
        cont2.get_c()->p();
    }
    std::cout << std::endl;
    {
       polyglot::ada::arrays::polyglot_array<test::Cont> arr = test::get_arr();
       test::foo(arr);
       test::foo(arr);
       test::foo(arr);
    }
    {
        test::Cont c1;
        test::Cont c2;
        std::cout << (c1 == c2) << "\n";
    }
}
