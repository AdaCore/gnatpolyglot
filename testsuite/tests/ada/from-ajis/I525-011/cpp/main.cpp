#include <iostream>

#include "p_lists.h"

int main() {
    p::lists::List l;

    l.append(1, 1);
    l.append(2, 1);
    l.append(3, 1);
    l.append(4, 1);
    l.append(5, 1);

    p::lists::Cursor c = l.first();

    while (c != p::lists::get_no_element()) {
        std::cout << c.element() << std::endl;

        c = c.next();
    }
    return 0;
}
