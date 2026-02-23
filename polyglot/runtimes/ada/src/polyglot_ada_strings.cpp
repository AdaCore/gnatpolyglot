#include "polyglot_ada_strings.h"
#include <cstdint>
#include <stdexcept>

using namespace polyglot::ada::arrays;

namespace polyglot::ada::strings {

extern "C" char *polyglot__ada__strings_to_c_chars_ptr(string_data data);
extern "C" string_data polyglot__ada__strings_from_c_chars_ptr(const char *);
extern "C" void polyglot__ada__strings_free_c_chars_ptr(char *);

polyglot_string::polyglot_string(const char *str)
    : _data(polyglot__ada__strings_from_c_chars_ptr(str)) {}

extern "C" void polyglot__ada__strings__string_free(void *);

polyglot_string::~polyglot_string() {
    polyglot__ada__strings__string_free(&this->_data);
}

extern "C" void *polyglot__ada__strings__string_get(string_data, std::int32_t i);
char &polyglot_string::at(std::int32_t index) {
    if (index < this->_data.begin || index > this->_data.end)
        throw std::out_of_range{"polyglot::ada::strings::string::at"};
    return polyglot_string::operator[](index);
}
void polyglot_string::set(std::int32_t index, char new_val) {
    at(index) = new_val;
}

char &polyglot_string::operator [](std::int32_t index) {
    return *static_cast<char *>(polyglot__ada__strings__string_get(this->_data, index));
}
char polyglot_string::operator [](std::int32_t index) const {
    return *static_cast<char *>(polyglot__ada__strings__string_get(this->_data, index));
}

std::string to_string(const polyglot_string &arr) {
  char *ptr = polyglot__ada__strings_to_c_chars_ptr(arr.data_());
  std::string res = ptr;
  polyglot__ada__strings_free_c_chars_ptr(ptr);
  return res;
}

polyglot_string from_string(const std::string &str) {
  return polyglot_string(polyglot__ada__strings_from_c_chars_ptr(str.data()));
}

} // namespace polyglot::ada::strings
