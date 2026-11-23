#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "test.h"
#include <iostream>
#include <string>

namespace ps = gnatpolyglot::ada::strings;
using namespace gnatpolyglot::ada::arrays;

static ps::polyglot_string size_string(const std::string &string, int size) {
    if ((int) string.length() == size) {
        std::cout << "*** sizeString making SAME size "
                  << size << " /"
                  << string.length() << " /"
                  << string << "/\n";
        return ps::from_string(string);
    } else if ((int) string.length() > size) {
        std::string smaller = string.substr(0, size);
        std::cout << "*** sizeString making SMALLER size "
                  << size << " /"
                  << string.length() << " /"
                  << smaller.length() << " /"
                  << smaller << "/\n";
        return ps::from_string(smaller);
    } else {
        int filler_count = size - string.length();
        std::string filled = string + std::string(filler_count, ' ');
        std::cout << "*** sizeString making BIGGER size "
                  << size << " /"
                  << string.length() << " /"
                  << filled.length() << " /"
                  << filled << "/\n";
        return ps::from_string(filled);
    }
}

int main() {
    // Track_Type / Track_Number_Type have no default constructor in the
    // generated API: build them with placeholder component values.
    test::TrackType t(0.0f, 0.0f, polyglot_array<test::TrackNumberType>(0, 25));

    test::TrackNumberType null_tn(-1, ps::from_string("               "));
    null_tn.set_source_type(0);
    ps::polyglot_string invalid_tn = size_string("XXX", 15);
    null_tn.set_tn(invalid_tn);

    for (int i = t.get_all_tns()->get_begin(); i <= t.get_all_tns()->get_end(); i++) {
        t.get_all_tns()->set(i, null_tn);
    }

    std::cout << "ELEM: " << ps::to_string(*t.get_all_tns()->get(1)->get_tn()) << "\n";

    return 0;
}
