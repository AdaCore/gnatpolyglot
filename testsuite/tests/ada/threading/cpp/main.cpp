#include <atomic>
#include <iostream>
#include <string>
#include <thread>
#include <vector>

#include "test.h"

std::atomic<int> counter{0};

void f(int n) {
    try {
        test::get_exception(n);
    } catch (const test::Exc &e) {
        counter += std::stoi(e.what());
    }
    test::unregister();
}

int main() {
    std::vector<std::thread> threads;
    int reference = 0;
    for (int i = 0; i < 1000; i++) {
        threads.emplace_back(f, i);
        reference += i;
    }
    for (int i = 0; i < 1000; i++) {
        threads[i].join();
    }
    std::cout << counter << " == " << reference << "\n";
}
