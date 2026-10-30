#include <iostream>
#include "sample.h"

class GuestKnown : public sample::Known {
public:
    GuestKnown(int n) : sample::Known(n, this) {}
};

class GuestChild : public sample::KnownChild {
public:
    GuestChild(int n) : sample::KnownChild(n, this) {}
};

class GuestForward : public sample::Forward {
public:
    GuestForward(int n) : sample::Forward(n, this) {}
};

class GuestDefaulted : public sample::Defaulted {
public:
    GuestDefaulted() : sample::Defaulted(this) {}
};

class GuestPlain : public sample::Plain {
public:
    GuestPlain() : sample::Plain(this) {}
};

int main() {
    sample::Known known(3);
    sample::KnownChild child(4);
    sample::Forward forward(8);
    GuestKnown guest(5);
    GuestChild guest_child(6);
    GuestForward guest_forward(7);
    GuestDefaulted defaulted;
    GuestPlain plain;
    std::cout << "known: " << known.get_n() << " " << child.get_n()
              << " " << forward.get_n() << '\n';
    std::cout << "shadows: " << guest.get_n() << " " << guest_child.get_n()
              << " " << guest_forward.get_n() << " " << defaulted.get_n() << '\n';
}
