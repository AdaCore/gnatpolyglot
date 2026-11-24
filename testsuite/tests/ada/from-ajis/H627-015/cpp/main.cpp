#include "test.h"
#include <iostream>

class RecChild : public test::ARecordTagged {
public:
    RecChild() : test::ARecordTagged(this) {}
};

int main() {
    test::ARecord rec;
    gnatpolyglot::polyglot_ptr<test::ARecordTagged> rec2 = test::get_a_record_tagged();
    test::AType typ;

    test::call_a_proc(typ, rec, *rec2);

    //  We used not to do properly the copy on the in out mode, and lose
    //  the value modification in the process, displaying 999 instead of
    //  1024.
    std::cout << rec.get_a_value() << ", " << rec2->get_a_value() << "\n";

    //  Check that it still works with extended types.

    test::ARecordTagged rec3;
    RecChild rec4_obj;
    test::ARecordTagged &rec4 = rec4_obj;

    test::call_a_proc(typ, rec, rec3);
    test::call_a_proc(typ, rec, rec4);

    std::cout << rec3.get_a_value() << "\n";
    std::cout << rec4.get_a_value() << "\n";
    return 0;
}
