#include "gnatpolyglot_ada_arrays.h"
#include "test.h"

#include <iostream>
#include <string>
#include <vector>

class OtherChild : public test::Root {
public:
    OtherChild(const std::string &data) : test::Root(4, 2, this), _str(data) {}

    void p1() const override{
        std::cout << _str << "\n";
    }

    test::Rec f() const override {
       return test::Rec(21);
    }

    gnatpolyglot::ada::arrays::polyglot_array<int32_t>
    f_arr(const gnatpolyglot::ada::arrays::polyglot_array<int32_t> &a, int &i) const override {
        gnatpolyglot::ada::arrays::polyglot_array<int32_t> res(a.get_begin(), a.get_end());
        for (int i = res.get_begin(); i <= res.get_end(); i++) {
            res.set(i, a.get(i) + 2);
        }
        std::cout << "Returning from C++: ";
        for (int i = res.get_begin(); i <= res.get_end(); i++) {
            std::cout << res.get(i) << ", ";
        }
        std::cout << "\n";
        i += 2;
        return res;
    }

private:
    std::string _str;
};

class GrandChild : public test::Child {
public:
    GrandChild(const std::vector<int> &data) : test::Child(4, 2, 1, this), _vec(data) {}

    void p1() const override {
        for (int i : _vec) {
            std::cout << i << " ";
        }
        std::cout << "\n";
    }

    void p2(const test::Rec &rec, int32_t i) const override {
        std::cout << "P2 from C++ " << i << ": " << rec.get_i() << "\n";
    }

    test::Rec f() const override {
        return test::Rec(100);
    }

private:
    std::vector<int> _vec;
};

void test_array(test::Root &r, gnatpolyglot::ada::arrays::polyglot_array<int32_t> input) {
    int i = 0;
    gnatpolyglot::ada::arrays::polyglot_array<int32_t> res = test::f_arr_disp(r, input, i);
    std::cout << "Got in C++: ";
    for (int i = res.get_begin(); i <= res.get_end(); i++) {
        std::cout << res.get(i) << ", ";
    }
    std::cout << "\n" << "i = " << i << "\n";

}

int main() {
    test::Root root(1, 2);

    root.p1();

    test::Child child(3, 4, 5);

    child.p1();

    OtherChild other("c++ inherited");
    other.p1();

    test::Root &ref = other;
    ref.p1();

    GrandChild grand({3, 4, 5});
    grand.p1();
    test::Child &gref = grand;
    gref.p1();
    test::Root &ref2 = gref;
    ref2.p1();

    test::p_root(root);
    test::p_root(child);
    test::p_root(other);
    test::p_root(grand);

    test::Rec r1 = test::f_rec(root);
    std::cout << "r1 = " << r1.get_i() << "\n";
    test::Rec r2 = test::f_rec(ref);
    std::cout << "r2 = " << r2.get_i() << "\n";
    test::Rec r3 = test::f_rec(grand);
    std::cout << "r3 = " << r3.get_i() << "\n";

    test::p2_child(child, r1, 1);
    test::p2_child(gref, r2, 1);

    gnatpolyglot::ada::arrays::polyglot_array<int32_t> arr(1, 5);
    for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
        arr.set(i, i);
    }
    test_array(root, arr);
    test_array(other, arr);
}
