#include "test.h"
#include "gnatpolyglot_ada_strings.h"
#include <iostream>
#include <string>

int main() {
    bool failed = false;
    const std::string name = "TEST.TEST_EXCEPTION";
    const std::string message = "Test_Exception from Ada in Test_Symbolic_Traceback_From_Java";
    const std::string location = "at test.adb:40";
    auto information = gnatpolyglot::ada::strings::to_string(
        test::test_exception_information_from_java());
    if (information.find(name) == std::string::npos) {
        std::cout << "exception information does'nt contain " << name << "\n";
        failed = true;
    }
    if (information.find(message) == std::string::npos) {
        std::cout << "exception information does'nt contain " << message << "\n";
        failed = true;
    }
    auto traceback = gnatpolyglot::ada::strings::to_string(
        test::test_symbolic_traceback_from_java());
    if (traceback.find(location) == std::string::npos) {
        std::cout << "symbolic traceback does'nt contain " << location << "\n";
        failed = true;
    }
    if (failed) {
        std::cout << information << "\n" << traceback << "\n";
    } else {
        std::cout << "OK";
    }
    std::cout.flush();
}
