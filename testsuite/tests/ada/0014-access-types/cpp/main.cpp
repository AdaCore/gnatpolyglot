#include <cassert>
#include <iostream>
#include <stdexcept>

#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ptr.h"
#include "test.h"
#include "lists.h"

using namespace gnatpolyglot::ada::arrays;

class Dummy {
public:
    bool is_shadow() const {
        return false;
    }

    void release_() {}
};

class DummyChild : public Dummy {
};

void test_polyglot_ptr() {
    {
        test::Rec rec(1);
        gnatpolyglot::polyglot_ptr<test::Rec> ptr1(rec);
        assert(ptr1.get_owner() == gnatpolyglot::memory_owner::STATIC);
        assert(ptr1.use_count() == 1);
    }
    {
        gnatpolyglot::polyglot_ptr<test::Rec> ptr1(nullptr);
        {
            gnatpolyglot::polyglot_ptr<test::Rec> ptr2(new test::Rec(1), gnatpolyglot::memory_owner::USER);
            ptr1 = ptr2;
            assert(ptr1.use_count() == 2);
        }
        assert(ptr1.use_count() == 1);
        assert(ptr1.get() != nullptr);
        ptr1.reset();
        assert(ptr1.get() == nullptr);
    }
    {
        gnatpolyglot::polyglot_ptr<Dummy> ptr1(nullptr);
        gnatpolyglot::polyglot_ptr<DummyChild> ptr2(nullptr);
        ptr1 = ptr2;
        assert(ptr1.use_count() == 0);
    }
}

void simple_rec() {
    test::Rec rec(1);
    gnatpolyglot::polyglot_ptr<test::Rec> ptr1(rec);
    auto other = test::rec_f(ptr1);
    std::cout << ptr1->get_i() << "\n";
    std::cout << other->get_i() << "\n";

    test::rec_p(other);
    std::cout << other->get_i() << "\n";
    test::rec_p(nullptr);
    std::cout << other->get_i() << "\n";
    test::rec_p(nullptr);
    std::cout << other->get_i() << "\n";

    try {
        other.set_owner(gnatpolyglot::memory_owner::USER);
        test::rec_p(other);
    } catch (const gnatpolyglot::ada::exceptions::ConstraintError &e) {
        std::cout << e.what() << "\n";
    }

    std::cout << other->get_i() << "\n";
    other.set_owner(gnatpolyglot::memory_owner::LIBRARY);
    test::rec_in_out(other);
    std::cout << other->get_i() << "\n";
    other.set_owner(gnatpolyglot::memory_owner::USER);
}

void use_free() {
    gnatpolyglot::polyglot_ptr<test::Rec> ptr1(
          new test::Rec(1), gnatpolyglot::memory_owner::LIBRARY);
    test::free(ptr1);
}

void recursive_rec() {
    lists::List list(gnatpolyglot::polyglot_ptr<lists::ListItem>(nullptr));
    gnatpolyglot::polyglot_ptr<lists::ListItem> item1(new lists::ListItem(1));
    gnatpolyglot::polyglot_ptr<lists::ListItem> item2(new lists::ListItem(2));

    list.push(item1);
    std::cout << list.get(0)->get_value() << "\n";

    list.push(item2);
    std::cout << list.get(1)->get_value() << "\n";
    std::cout << item1->get_next()->get_next().get() << "\n";

    gnatpolyglot::polyglot_ptr<lists::ListItem> item3(new lists::ListItem(3));
    item2->set_next(item3);
    std::cout << list.get(2)->get_value() << "\n";

    item1.set_owner(gnatpolyglot::memory_owner::USER);
    item2.set_owner(gnatpolyglot::memory_owner::USER);
    item3.set_owner(gnatpolyglot::memory_owner::USER);
}

void print_array(const polyglot_array<int> &arr) {
    std::cout << "{ ";
    for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
        std::cout << arr.get(i) << ", ";
    }
    std::cout << "}\n";
}

void array() {
    polyglot_array<int> arr(1, 4);
    for (int i = arr.get_begin(); i <= arr.get_end(); i++) {
        arr.set(i, i);
    }
    gnatpolyglot::polyglot_ptr<polyglot_array<int>> ptr1(arr);
    auto other = test::arr_f(ptr1);
    print_array(*ptr1);
    print_array(*other);

    test::arr_p(other);
    test::arr_p(other);
    print_array(*ptr1);
    print_array(*other);

    test::arr_p(nullptr);

    test::arr_p_in_out(other);
    print_array(*ptr1);
    print_array(*other);
    other.set_owner(gnatpolyglot::memory_owner::USER);
}

int main() {
    test_polyglot_ptr();
    simple_rec();
    use_free();
    std::cout << "\n";
    recursive_rec();
    std::cout << "\n";
    array();
}
