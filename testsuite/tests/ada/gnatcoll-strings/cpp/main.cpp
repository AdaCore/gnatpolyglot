#include "gnatpolyglot_ada_arrays.h"
#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ptr.h"
#include <cassert>
#include <gnatcoll_strings.h>
#include <iostream>

using namespace gnatpolyglot;
using namespace gnatpolyglot::ada::strings;
using namespace gnatpolyglot::ada::arrays;
using namespace gnatcoll::strings;

const char *CSHORT = "ABCD";
const char *CLONG = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

polyglot_string SHORT{"ABCD"};
polyglot_string LONG{"ABCDEFGHIJKLMNOPQRSTUVWXYZ"};

void testAppend() {
    Xstring s{};
    Xstring s2{};
    s.set(SHORT);
    assert(s == SHORT);

    s.set(LONG);

    s2 = s;
    s.append(SHORT);
    assert(s2 == LONG);
    assert(s == polyglot_string{(std::string(CLONG) + std::string(CSHORT)).data()});

    s.set(SHORT);
    assert(s == SHORT);
    assert(s2 == LONG);

    s.set(LONG);

    s.set(LONG);
    s2 = s;
    s.slice(2, 3);
    s.append(SHORT);
    assert(s2 == LONG);
    assert(s ==
           polyglot_string{
               (std::string(CLONG).substr(1, 2) + std::string(CSHORT)).data()});
}

int main() {
    testAppend();
}
