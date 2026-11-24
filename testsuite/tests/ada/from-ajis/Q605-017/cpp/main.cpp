#include "hello.h"
#include <iostream>

// C++ transposition of AJIS Q605-017.
// AJIS Parent_Type.Ref / Child_Type.Ref map to polyglot_ptr<ParentType> /
// polyglot_ptr<ChildType>; objects created from C++ live on the stack and are
// passed to Ada as STATIC pointers (AJIS set them to Owner.NATIVE).

using gnatpolyglot::polyglot_ptr;

class JavaParent : public hello::ParentType {
public:
    JavaParent() : hello::ParentType(this) {}

    void proc(polyglot_ptr<hello::ParentType> &object) const override {
        say();
        object->say();
    }

    void say() const override {
        std::cout << "Java Parent\n";
    }
};

class JavaChild : public hello::ChildType {
public:
    JavaChild() : hello::ChildType(this) {}

    // As in the AJIS test, this overrides the Child_Type_Access overload of
    // Proc, not the inherited Parent_Type_Access one.
    void proc(polyglot_ptr<hello::ChildType> &object) const override {
        say();
        object->say();
    }

    void say() const override {
        std::cout << "Java Child\n";
    }
};

static void run(hello::ParentType &p, hello::ParentType &p1,
                hello::ParentType &p2) {
    polyglot_ptr<hello::ParentType> ref(p);
    p.proc(ref);
    ref = polyglot_ptr<hello::ParentType>(p1);
    p.proc(ref);
    ref = polyglot_ptr<hello::ParentType>(p2);
    p.proc(ref);
}

static void run(hello::ChildType &p, hello::ChildType &p1,
                hello::ChildType &p2) {
    polyglot_ptr<hello::ParentType> ref(p);
    p.proc(ref);
    ref = polyglot_ptr<hello::ParentType>(p1);
    p.proc(ref);
    ref = polyglot_ptr<hello::ParentType>(p2);
    p.proc(ref);
}

int main() {
    auto ap_view = hello::get_ada_parent();
    auto ac_view = hello::get_ada_child();
    hello::ParentType &ap = *ap_view;
    hello::ChildType &ac = *ac_view;
    hello::ParentType p;
    hello::ChildType c;
    JavaParent jp;
    JavaChild jc;
    run(ap, p, jp);
    run(p, jp, ap);
    run(jp, ap, p);
    run(ac, c, jc);
    run(c, jc, ac);
    run(jc, ac, c);
    return 0;
}
