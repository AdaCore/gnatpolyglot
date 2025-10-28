#include <iostream>

#include "polyglot_ada_arrays.h"
#include "polyglot_ada_strings.h"
#include "polyglot_ptr.h"
#include "test.h"

using namespace polyglot::ada::arrays;
using namespace polyglot::ada::strings;

class Child : public test::Root {
public:
    Child () : test::Root(this) {}

    polyglot::polyglot_ptr<test::Rec> root_rec(
            polyglot::polyglot_ptr<test::Rec> acc) const override {
       std::cout << "C++ got: " << acc->get_i() << "\n";
        return polyglot::polyglot_ptr<test::Rec>(new test::Rec(40));
    }

    polyglot::polyglot_ptr<polyglot_array<int>> root_arr(
            polyglot::polyglot_ptr<polyglot_array<int>> acc) const override {
        std::cout << "C++ got: {";
        for (int i = acc->get_begin(); i <= acc->get_end(); i++) {
           std::cout << acc->get(i) << ", ";
        }
        std::cout << "}\n";
        polyglot::polyglot_ptr<polyglot_array<int>>
            ptr(new polyglot_array<int>(3, 9));
        for (int i = 3; i <= 9; i++) {
            ptr->set(i, i);
        }
        return ptr;
    }

    polyglot::polyglot_ptr<polyglot_string>
    root_str(polyglot::polyglot_ptr<polyglot_string> acc) const override {
        std::cout << "C++ got: " << to_string(*acc) << "\n";
        return polyglot::polyglot_ptr<polyglot_string>(
                new polyglot_string("From C++"));
    }

    void root_rec_p(polyglot::polyglot_ptr<test::Rec> &acc) const override {
       std::cout << "C++ got: " << acc->get_i() << "\n";
       acc.reset(new test::Rec(100));
    }

    void root_arr_p(
            polyglot::polyglot_ptr<polyglot_array<int>> &acc) const override {
        std::cout << "C++ got: {";
        for (int i = acc->get_begin(); i <= acc->get_end(); i++) {
           std::cout << acc->get(i) << ", ";
        }
        std::cout << "}\n";
        polyglot::polyglot_ptr<polyglot_array<int>>
            ptr(new polyglot_array<int>(10, 20));
        for (int i = ptr->get_begin(); i <= ptr->get_end(); i++) {
            ptr->set(i, i);
        }
        acc = ptr;
    }

    void
    root_str_p(polyglot::polyglot_ptr<polyglot_string> &acc) const override {
        std::cout << "C++ got: " << to_string(*acc) << "\n";
        acc = new polyglot_string("From C++");
    }
};

void dynamic_dispatch() {
    Child c;
    test::Rec r(42);
    polyglot::polyglot_ptr<test::Rec> rec_ptr(r);
    polyglot::polyglot_ptr<test::Rec> rec = call_root_rec(c, rec_ptr);
    std::cout << rec->get_i() << "\n";
    rec.set_owner(polyglot::memory_owner::USER);

    polyglot_array<int> a(1, 10);
    for (int i = a.get_begin(); i <= a.get_end(); i++) {
        a.set(i, i);
    }
    polyglot::polyglot_ptr<polyglot_array<int>> arr_ptr(a);

    polyglot::polyglot_ptr<polyglot_array<int>> arr = test::call_root_arr(c, arr_ptr);
    std::cout << arr->get_begin() << " .. " << arr->get_end() << " = { ";
    for (int i = arr->get_begin(); i <= arr->get_end(); i++) {
        std::cout << arr->get(i) << ", ";
    }
    arr.set_owner(polyglot::memory_owner::USER);
    std::cout << "}\n";
}

void in_out() {
    Child c;
    test::Rec r(42);
    polyglot::polyglot_ptr<test::Rec> rec_ptr(r);
    call_root_rec_p(c, rec_ptr);
    std::cout << rec_ptr->get_i() << "\n";
    rec_ptr.set_owner(polyglot::memory_owner::USER);

    polyglot_array<int> a(1, 10);
    for (int i = a.get_begin(); i <= a.get_end(); i++) {
        a.set(i, i);
    }
    polyglot::polyglot_ptr<polyglot_array<int>> arr_ptr(a);
    test::call_root_arr_p(c, arr_ptr);
    std::cout << arr_ptr->get_begin() << " .. " << arr_ptr->get_end() << " = { ";
    for (int i = arr_ptr->get_begin(); i <= arr_ptr->get_end(); i++) {
        std::cout << arr_ptr->get(i) << ", ";
    }
    arr_ptr.set_owner(polyglot::memory_owner::USER);
    std::cout << "}\n";
}

void strings() {
    Child c;
    polyglot_string str("begin");
    polyglot::polyglot_ptr<polyglot_string> ptr1(str);
    polyglot::polyglot_ptr<polyglot_string> ptr2 = test::call_root_str(c, ptr1);
    std::cout << "Got from Ada: " << to_string(*ptr2) << "\n";
    ptr2.set_owner(polyglot::memory_owner::USER);
    ptr2.reset(&str, polyglot::memory_owner::STATIC);
    test::call_root_str_p(c, ptr2);
    std::cout << "Got from Ada: " << to_string(*ptr2) << "\n";
    ptr2.set_owner(polyglot::memory_owner::USER);
    ptr2.reset();
}

int main() {
    dynamic_dispatch();
    std::cout << "\n";
    in_out();
    std::cout << "\n";
    strings();
}
