#include "test.h"
#include <iostream>

int main() {
    test::call([](char &v1, char &v2){
        std::cout << "C++: " << v2 << "\n";
        v1 = 'w';
        v2 = 'e';
    });
}
