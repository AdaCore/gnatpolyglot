//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot_ada_strings.h"
#include "gnatpolyglot_ada_exceptions.h"
#include <cstdint>

using namespace gnatpolyglot::ada::arrays;

namespace gnatpolyglot::ada::strings {

extern "C" char *gnatpolyglot__ada__strings_to_c_chars_ptr(string_data data);
extern "C" string_data
gnatpolyglot__ada__strings_from_c_chars_ptr(const char *);
extern "C" void gnatpolyglot__ada__strings_free_c_chars_ptr(char *);

polyglot_string::polyglot_string(const char *str)
    : _data(gnatpolyglot__ada__strings_from_c_chars_ptr(str)) {}

extern "C" void gnatpolyglot__ada__strings__string_free(void *);

polyglot_string::~polyglot_string() {
  gnatpolyglot__ada__strings__string_free(&this->_data);
}

extern "C" void *gnatpolyglot__ada__strings__string_get(string_data,
                                                        std::int32_t i);
char &polyglot_string::at(std::int32_t index) {
    if (index < this->_data.begin || index > this->_data.end)
        throw gnatpolyglot::ada::exceptions::ConstraintError("polyglot::ada::strings::string::at");

    return polyglot_string::operator[](index);
}
void polyglot_string::set(std::int32_t index, char new_val) {
  at(index) = new_val;
}

char &polyglot_string::operator[](std::int32_t index) {
  return *static_cast<char *>(
      gnatpolyglot__ada__strings__string_get(this->_data, index));
}
char polyglot_string::operator[](std::int32_t index) const {
  return *static_cast<char *>(
      gnatpolyglot__ada__strings__string_get(this->_data, index));
}

#if __has_include(<string>)

std::string to_string(const polyglot_string &arr) {
  char *ptr = gnatpolyglot__ada__strings_to_c_chars_ptr(arr.data_());
  std::string res = ptr;
  gnatpolyglot__ada__strings_free_c_chars_ptr(ptr);
  return res;
}

polyglot_string from_string(const std::string &str) {
  return polyglot_string(
      gnatpolyglot__ada__strings_from_c_chars_ptr(str.data()));
}

#endif

} // namespace gnatpolyglot::ada::strings
