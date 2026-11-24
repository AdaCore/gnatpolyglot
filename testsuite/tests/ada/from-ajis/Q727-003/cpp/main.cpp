#include <iostream>
#include <string>

#include "hello.h"

using gnatpolyglot::polyglot_ptr;

class Child : public hello::DeclaredEarly {
public:
    Child(const std::string &text) : hello::DeclaredEarly(this), f_text(text) {}

    std::string f_text;
};

int main() {
    hello::DeclaredEarlyNotTagged dent(0);
    dent.set_year(1983);
    hello::ReferenceTypeNotTagged rtnt((polyglot_ptr<hello::DeclaredEarlyNotTagged>(dent)));
    if (rtnt.get_ref()->get_year() == 1983) {
        Child de("OK");
        hello::ReferenceType rt((polyglot_ptr<hello::DeclaredEarly>(de)));
        std::cout << dynamic_cast<Child &>(*rt.get_ref()).f_text << std::endl;
    }
    return 0;
}
