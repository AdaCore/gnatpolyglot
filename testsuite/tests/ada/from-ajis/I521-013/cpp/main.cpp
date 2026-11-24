#include <iostream>

#include "lib_inst.h"
#include "p.h"
#include "p_inst_1.h"
#include "p_inst_1_gen_inst.h"
#include "p_inst_2.h"

int main() {
    p::inst_1::R r1(0);
    r1.store(99);
    std::cout << r1.get() << std::endl;

    p::inst_2::R r2(p::MyRec(0));
    p::MyRec rec(0);
    rec.set_val(77);
    r2.store(rec);
    std::cout << r2.get().get_val() << std::endl;

    r2 = p::create();
    rec = p::MyRec(0);
    rec.set_val(66);
    r2.store(rec);
    std::cout << r2.get().get_val() << std::endl;

    p::inst_proc(rec);

    lib_inst::R r3(0);
    r3.store(42);
    std::cout << r3.get() << std::endl;

    p::inst_1::gen_inst::set_x(43);
    std::cout << p::inst_1::gen_inst::get_x() << std::endl;
    return 0;
}
