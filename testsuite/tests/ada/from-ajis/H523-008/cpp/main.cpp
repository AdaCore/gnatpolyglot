#include <iostream>
#include "parent.h"
#include "test.h"

int main() {
    parent::Rec rec;
    gnatpolyglot::polyglot_ptr<parent::Rec> ptr(rec);
    test::set_x(ptr);
    std::cout << "CREATION OF REC WORKED\n";
}
