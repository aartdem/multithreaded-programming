#include <iostream>
#include <vector>

#include "ThreadPool.h"

int main() {

    ThreadPool pool(4);
    std::vector <std::future<int>> results;

    int counter = 0;

    for (int i = 0; i < 8; ++i) {
        results.emplace_back(
                pool.enqueue([&counter] {
                    counter++;
                    return counter;
                })
        );
    }

    for (auto &&result: results)
        std::cout << result.get() << ' ';
    std::cout << std::endl;

    return 0;
}