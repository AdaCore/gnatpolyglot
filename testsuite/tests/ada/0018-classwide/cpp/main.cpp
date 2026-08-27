#include "test.h"
#include <cassert>
#include <iostream>

class CppClass : public test::Root {
public:
    CppClass(int i) : test::Root(i, this) {}

    CppClass(const CppClass&, gnatpolyglot::data *data)
        : test::Root(data) {}

    CppClass *internal_clone_(gnatpolyglot::data *data) {
        return new CppClass(*this, data);
    }

    gnatpolyglot::polyglot_ptr<test::Root>
    f_root(const test::Root &r2) const override {
        std::cout << "f_root: "
                  << (dynamic_cast<const ::test::Child *>(&r2) != nullptr)
                  << "\n";
        return gnatpolyglot::polyglot_ptr<test::Root>(new test::Root(r2));
    }

    void p_root_a(gnatpolyglot::polyglot_ptr<test::Root> r2) const override {
        std::cout << "p_root_a: "
                  << (dynamic_cast<const ::test::Child *>(r2.get()) != nullptr)
                  << "\n";
    }

    void p_out_root_a(gnatpolyglot::polyglot_ptr<test::Root> &r2) const override {
        std::cout << "p_out_root_a: "
                  << (dynamic_cast<const ::test::Child *>(r2.get()) != nullptr)
                  << "\n";
    }
};

int main() {
    assert(dynamic_cast<::test::Child*>(test::get_root().get()) == nullptr);
    assert(dynamic_cast<::test::Child*>(test::get_child().get()) != nullptr);

    gnatpolyglot::polyglot_ptr<test::Root> ptr1 = test::get_root_a();
    assert(dynamic_cast<::test::Child*>(ptr1.get()) == nullptr);
    ptr1.set_owner(gnatpolyglot::memory_owner::USER);
    gnatpolyglot::polyglot_ptr<test::Root> ptr2 = test::get_child_a();
    assert(dynamic_cast<::test::Child*>(ptr2.get()) != nullptr);
    ptr2.set_owner(gnatpolyglot::memory_owner::USER);

    {
        gnatpolyglot::polyglot_ptr<test::Root> ptr(nullptr);
        test::get_root(ptr);
        assert(ptr);
        assert(dynamic_cast<::test::Child*>(ptr.get()) == nullptr);
        test::get_child(ptr);
        assert(dynamic_cast<::test::Child*>(ptr.get()) != nullptr);
        ptr.set_owner(gnatpolyglot::memory_owner::USER);
    }
    {
        CppClass c{4};
        test::call_f_root(c, test::Root(1));
        test::call_f_root(c, test::Child(1, 2));
        test::Root tmp_r{1};
        test::call_p_root_a(c, gnatpolyglot::polyglot_ptr<test::Root>{tmp_r});
        gnatpolyglot::polyglot_ptr<test::Root> tmp_ptr1{tmp_r};
        test::call_p_out_root_a(c, tmp_ptr1);
        test::Child tmp_c{1, 2};
        test::call_p_root_a(c, gnatpolyglot::polyglot_ptr<test::Root>{tmp_c});
        gnatpolyglot::polyglot_ptr<test::Root> tmp_ptr2{tmp_c};
        test::call_p_out_root_a(c, tmp_ptr2);

        gnatpolyglot::polyglot_ptr<test::Root> cloned = test::clone(c);
        assert(dynamic_cast<CppClass*>(cloned.get()) != nullptr);
        cloned->set_i(5);
        std::cout << c.get_i() << " != " << (cloned->get_i()) << "\n";

        gnatpolyglot::polyglot_ptr<test::Root> c2 {new CppClass(5)};
        test::free(c2);
        std::cout << c2.get() << "\n";
    }
    {
        CppClass c(2);
        auto f = [](const test::Root& r) {
            std::cout << "call_callback: "
                      << (dynamic_cast<const ::test::Child *>(&r) != nullptr)
                      << (dynamic_cast<const CppClass *>(&r) != nullptr)
                      << "\n";
        };
        test::call_callback(f, test::Root(1));
        test::call_callback(f, test::Child(1, 2));
        test::call_callback(f, c);
    }
}
