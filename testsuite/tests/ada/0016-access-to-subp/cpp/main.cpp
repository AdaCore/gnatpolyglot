#include <cstdint>
#include <iostream>

#include "test.h"

using namespace gnatpolyglot::ada::arrays;

class Child : public test::T {
public:
    Child() : test::T (this) {}

    int32_t t_call(std::function<int32_t (int32_t, int32_t)> c) const override {
        return c(-1, -2);
    }
};

std::int32_t foo(std::int32_t i, std::int32_t j) {
  std::cout << "Hello!\n";
  return i + j;
}

int main() {
    {
        // Scalar callbacks
        std::cout << test::call(&foo) << "\n";
        std::cout << test::call([](int i, int j) { return i * j; }) << "\n";
        // Record callbacks
        test::Rec r = test::call([](test::Rec rec) {
            return test::Rec(rec.get_i() - 1, rec.get_j() - 2);
        });
        std::cout << "Rec{" << r.get_i() << ", " << r.get_j() << "}\n";
        // Array callbacks
        polyglot_array<test::Rec> arr = test::call([](const polyglot_array<test::Rec> &arr) {
            polyglot_array<test::Rec> res = arr;
            for (auto &rec : res) {
                rec.get_i() += 4;
                rec.get_j() += 8;
            }
            return res;
        });
        std::cout << "{" << "Rec{" << arr.get(1)->get_i() << ", " << arr.get(1)->get_j() << "}, "
                  << "{" << "Rec{" << arr.get(2)->get_i() << ", " << arr.get(2)->get_j() << "}}\n";
    }

    std::cout << std::endl;

    {
        // Scalar callbacks
        std::cout << test::get_callback_1()(10, 8) << "\n";
        // Record callbacks
        test::Rec r = test::get_callback_2()(test::Rec(10, 8));
        std::cout << "Rec{" << r.get_i() << ", " << r.get_j() << "}\n";
        // Array callbacks
        polyglot_array<test::Rec> arr1{1, 2};
        arr1.set(1, test::Rec{1, 2});
        arr1.set(2, test::Rec{3, 4});
        polyglot_array<test::Rec> arr2 = test::get_callback_3()(arr1);
        std::cout << "{" << "Rec{" << arr2.get(1)->get_i() << ", " << arr2.get(1)->get_j() << "}, "
                  << "{" << "Rec{" << arr2.get(2)->get_i() << ", " << arr2.get(2)->get_j() << "}}\n";
    }

    std::cout << std::endl;

    std::function<int32_t(int32_t, int32_t)> f = nullptr;
    test::get_callback(f);
    std::cout << f(10, 8) << "\n";

    test::T t{};
    Child c{};
    std::cout << test::call_t_call(t, &foo) << "\n";
    std::cout << test::call_t_call(c, &foo) << "\n";

}
