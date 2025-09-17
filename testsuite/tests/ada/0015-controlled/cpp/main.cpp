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
        cont.p();
        wrapper.get_c()->p();
    }
}
