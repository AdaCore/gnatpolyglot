#include "hello.h"
#include <iostream>

int main() {
    hello::lang_specific_databases_maps::Map map;
    hello::GeneralXrefDatabaseRecord db(map);
    std::cout << "OK\n";
}
