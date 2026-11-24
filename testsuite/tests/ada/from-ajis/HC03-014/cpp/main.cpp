#include "society.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ada_arrays.h"
#include <iostream>

namespace ps = gnatpolyglot::ada::strings;

int main() {
    // Name_Register is array(Unique_DNA_Codes=1..5) of Name(=String(1..18))
    auto reg = society::name_register();
    auto elem = reg->get(2);
    std::cout << ps::to_string(*elem) << "\n";
    return 0;
}
