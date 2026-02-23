#include <iostream>

#include <polyglot_ada_arrays.h>

#include "test.h"

int main() {

    int i = 1;
    test::p_int(i);
    test::p_int_out(i);
    test::p_int(i);

    test::Rec rec(1);
    rec.p_rec();
    rec.p_rec_out();
    rec.p_rec();

    polyglot::ada::arrays::polyglot_array<int> arr(1, 5);
    for (auto &i : arr) {
       i = 1;
    }
    test::p_arr(arr);
    test::p_arr_out(arr);
    test::p_arr(arr);
}
