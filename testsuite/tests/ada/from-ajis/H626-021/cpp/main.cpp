#include "test.h"

// Rec_F (access function return A_Rec) is bound as a
// std::function<polyglot_ptr<Rec>()>; the Java Rec_F_Child subclass
// becomes a lambda.
int main() {
    test::call_and_display([]() {
        test::Rec *result = new test::Rec(0, 0);
        result->set_a(999);
        result->set_b(888);

        // The access value is handed over to the Ada library.
        return gnatpolyglot::polyglot_ptr<test::Rec>(
            result, gnatpolyglot::memory_owner::LIBRARY);
    });
    return 0;
}
