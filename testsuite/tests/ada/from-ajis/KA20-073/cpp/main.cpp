#include "hello.h"

int main() {
    hello::set_const(5);
    hello::set_break(6);
    hello::p(hello::get_const() + hello::get_break());
    return 0;
}
