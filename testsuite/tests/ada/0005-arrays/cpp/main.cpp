#include <iostream>

#include "test.h"

using gnatpolyglot::ada::arrays::polyglot_array;

void test_integer_arrays() {
    std::cout << "-- integer arrays --\n";

    // A function returning an unconstrained array, owned on the C++ side.
    polyglot_array<int32_t> arr = test::f_u_1();
    std::cout << "bounds: " << arr.get_begin() << " " << arr.get_end() << "\n";
    std::cout << "content: [" << arr.get(1) << " " << arr.get(2) << " "
              << arr.get(3) << "]\n";
    std::cout << "sum: " << test::f_u_2(arr) << "\n";

    // Write elements back, then read them through a parameter.
    arr.set(1, 4);
    arr.set(2, 5);
    arr.set(3, 6);
    std::cout << "content: [" << arr.get(1) << " " << arr.get(2) << " "
              << arr.get(3) << "]\n";
    std::cout << "sum: " << test::f_u_2(arr) << "\n";

    // An `in out` parameter: Ada mutates the shared buffer in place.
    test::out_proc(arr);
    std::cout << "doubled:";
    for (auto &x : arr) {
        std::cout << " " << x;
    }
    std::cout << "\n";

    // An array allocated and filled on the C++ side (through the iterator).
    polyglot_array<int> owned(1, 5);
    int v = 0;
    for (auto &x : owned) {
        x = (v += 2);
    }
    std::cout << "owned:";
    for (auto &x : owned) {
        std::cout << " " << x;
    }
    std::cout << "\n";
}

void test_integer_widths() {
    std::cout << "-- integer widths --\n";

    polyglot_array<int8_t> bytes = test::make_bytes();
    std::cout << "bytes:";
    for (int i = bytes.get_begin(); i <= bytes.get_end(); i++) {
        std::cout << " " << (int) bytes.get(i);
    }
    std::cout << "\n";

    polyglot_array<int16_t> shorts = test::make_shorts();
    std::cout << "shorts:";
    for (int i = shorts.get_begin(); i <= shorts.get_end(); i++) {
        std::cout << " " << shorts.get(i);
    }
    std::cout << "\n";

    // The 64-bit values exceed 32 bits, proving the elements are not truncated.
    polyglot_array<int64_t> longs = test::make_longs();
    std::cout << "longs:";
    for (int i = longs.get_begin(); i <= longs.get_end(); i++) {
        std::cout << " " << longs.get(i);
    }
    std::cout << "\n";
}

void test_floating_point_arrays() {
    std::cout << "-- floating-point arrays --\n";

    polyglot_array<float> floats = test::make_floats();
    std::cout << "floats:";
    for (auto &x : floats) {
        std::cout << " " << x;
    }
    std::cout << "\n";
    std::cout << "sum: " << test::sum_floats(floats) << "\n";

    // An `in out` parameter mutates the shared buffer in place.
    test::scale_floats(floats);
    std::cout << "scaled:";
    for (auto &x : floats) {
        std::cout << " " << x;
    }
    std::cout << "\n";
    std::cout << "sum: " << test::sum_floats(floats) << "\n";

    // A float array allocated on the C++ side.
    polyglot_array<float> owned(1, 3);
    owned.set(1, 0.25f);
    owned.set(2, 0.5f);
    owned.set(3, 0.75f);
    std::cout << "owned:";
    for (auto &x : owned) {
        std::cout << " " << x;
    }
    std::cout << "\n";

    polyglot_array<double> doubles = test::make_doubles();
    std::cout << "doubles:";
    for (auto &x : doubles) {
        std::cout << " " << x;
    }
    std::cout << "\n";
    std::cout << "sum: " << test::sum_doubles(doubles) << "\n";
}

void test_boolean_arrays() {
    std::cout << std::boolalpha << "-- boolean arrays --\n";

    polyglot_array<bool> bools = test::make_bools();
    std::cout << "bools:";
    for (auto &x : bools) {
        std::cout << " " << x;
    }
    std::cout << "\n";
    std::cout << "any: " << test::any_true(bools) << "\n";

    // An `in out` parameter flips each element in place.
    test::negate_bools(bools);
    std::cout << "negated:";
    for (auto &x : bools) {
        std::cout << " " << x;
    }
    std::cout << "\n";
    std::cout << "any: " << test::any_true(bools) << "\n";

    // A boolean array allocated on the C++ side.
    polyglot_array<bool> owned(1, 2);
    owned.set(1, true);
    owned.set(2, false);
    std::cout << "owned:";
    for (auto &x : owned) {
        std::cout << " " << x;
    }
    std::cout << "\n";
}

void test_record_arrays() {
    std::cout << "-- record arrays --\n";

    polyglot_array<test::MyInt> arr = test::my_int_arr_func();
    std::cout << "bounds: " << arr.get_begin() << " " << arr.get_end() << "\n";
    std::cout << "content:";
    for (auto &el : arr) {
        std::cout << " " << el.get_i();
    }
    std::cout << "\n";

    // An `in out` parameter mutates the records in place.
    test::my_int_arr_proc(arr);
    std::cout << "tripled:";
    for (auto &el : arr) {
        std::cout << " " << el.get_i();
    }
    std::cout << "\n";
    test::print_image(arr);

    // A record array allocated and filled on the C++ side.
    polyglot_array<test::MyInt> owned(1, 3);
    int v = 0;
    for (auto &el : owned) {
        el = test::MyInt((v += 1) * 10);
    }
    std::cout << "owned:";
    for (auto &el : owned) {
        std::cout << " " << el.get_i();
    }
    std::cout << "\n";
}

int main() {
    test_integer_arrays();
    test_integer_widths();
    test_floating_point_arrays();
    test_boolean_arrays();
    test_record_arrays();
}
