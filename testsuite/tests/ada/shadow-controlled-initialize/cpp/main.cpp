#include <iostream>

#include "pkg.h"
#include "pkg_child.h"

class Pub_Child : public pkg::Pub {
public:
    Pub_Child() : pkg::Pub(this) {}
};

class Pub_Value_Child : public pkg::PubValue {
public:
    Pub_Value_Child() : pkg::PubValue(this) {}
    Pub_Value_Child(int32_t v) : pkg::PubValue(v, this) {}
};

class Priv_Child : public pkg::Priv {
public:
    Priv_Child() : pkg::Priv(this) {}
};

class Pub_Inherit_Child : public pkg::child::PubInherit {
public:
    Pub_Inherit_Child() : pkg::child::PubInherit(this) {}
};

class Pub_Override_Child : public pkg::child::PubOverride {
public:
    Pub_Override_Child() : pkg::child::PubOverride(this) {}
};

int main() {
    // Initialize must be called exactly once for each object
    std::cout << "Pub:" << std::endl;
    pkg::Pub pub;
    std::cout << "Pub_Child:" << std::endl;
    Pub_Child pub_child;
    std::cout << "Pub_Value():" << std::endl;
    pkg::PubValue pub_value;
    std::cout << "Pub_Value(1):" << std::endl;
    pkg::PubValue pub_value_1(1);
    std::cout << "Pub_Value_Child():" << std::endl;
    Pub_Value_Child pub_value_child;
    std::cout << "Pub_Value_Child(1):" << std::endl;
    Pub_Value_Child pub_value_child_1(1);
    std::cout << "Priv:" << std::endl;
    pkg::Priv priv;
    std::cout << "Priv_Child:" << std::endl;
    Priv_Child priv_child;
    std::cout << "Pub_Inherit:" << std::endl;
    pkg::child::PubInherit pub_inherit;
    std::cout << "Pub_Inherit_Child:" << std::endl;
    Pub_Inherit_Child pub_inherit_child;
    std::cout << "Pub_Override:" << std::endl;
    pkg::child::PubOverride pub_override;
    std::cout << "Pub_Override_Child:" << std::endl;
    Pub_Override_Child pub_override_child;
    return 0;
}
