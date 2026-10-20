#include <iostream>

#include "test.h"

class DDTRChild : public test::Ddtr{
public:
    DDTRChild() : test::Ddtr(this) {}
    DDTRChild(int a) : test::Ddtr(a, this) {}
};

int main() {
    test::Root r{1, 2};
    static_assert(std::is_same<const int &, decltype(r.get_a())>::value);
    std::cout << r.get_a() << " " << r.get_b() << "\n";

    test::Child1 c1{2};
    std::cout << c1.get_a()
              << " " << c1.get_b()
              << " " << c1.get_c()
              << "\n";

    test::Child2 c2{2, 3};
    std::cout << c2.get_a()
              << " " << c2.get_b()
              << "\n";

    test::R rec{3, "abc"};
    std::cout << gnatpolyglot::ada::strings::to_string(rec.get_s()) << "\n";
    try {
        test::R rec2{2, "abc"};
    } catch (const gnatpolyglot::ada::exceptions::ConstraintError &e) {
        std::cout << "caught: " << e.what() << "\n";
    }

    test::Wrapper null_ref{};
    std::cout << null_ref.get_acc().get() << "\n";
    test::Wrapper ref{gnatpolyglot::polyglot_ptr<test::R>{rec}};
    std::cout << gnatpolyglot::ada::strings::to_string(ref.get_acc()->get_s())
              << "\n";

    test::Priv priv_1{};
    test::Priv priv_2{1};
    std::cout << priv_2.get_i() << " " << priv_2.get_j_component() << "\n";

    test::Ddtr ddtr_1{};
    test::Ddtr ddtr_2{4};
    std::cout << DDTRChild{}.get_a() << "\n";
}
