#include "hello.h"
#include <gnatpolyglot_ada_strings.h>

namespace ps = gnatpolyglot::ada::strings;

// C++ shadow subclass overriding Msg to return " 2012" (as in the AJIS Java
// test's JavaMsg subclass).
class CppMsg : public hello::TaggedRecordType {
public:
    CppMsg() : hello::TaggedRecordType(this) {}
    CppMsg(gnatpolyglot::data *data) : hello::TaggedRecordType(data) {}

    ps::polyglot_string msg() const override {
        return ps::from_string(" 2012");
    }

    hello::TaggedRecordType *internal_clone_(gnatpolyglot::data *data) override {
        return new CppMsg(data);
    }
};

int main() {
    // Plain record: set I = 1 + 1994 = 1995, call Say
    hello::RecordType r;
    r.set_i(r.get_i() + 1994);
    r.say();

    // Tagged record: set I = 1 + 2004 = 2005, call class-wide Say
    hello::TaggedRecordType tr;
    tr.set_i(tr.get_i() + 2004);
    hello::say(tr);

    // C++ shadow subclass overriding Msg: Say dispatches to overridden Msg
    CppMsg jm;
    hello::say(jm);

    return 0;
}
