//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#include "gnatpolyglot_ada_arrays.h"
#include <cstdint>

#define MAKE_ARRAY_FUNCTIONS(CTYPE, ADATYPE)                                   \
  extern "C" array_data                                                        \
  gnatpolyglot__ada__arrays__native__##ADATYPE##_array_alloc(int, int);        \
  template <>                                                                  \
  polyglot_array<CTYPE>::polyglot_array(int begin, int end)                    \
      : _data(gnatpolyglot__ada__arrays__native__##ADATYPE##_array_alloc(      \
            begin, end)) {}                                                    \
                                                                               \
  extern "C" void gnatpolyglot__ada__arrays__native__##ADATYPE##_array_free(   \
      array_data);                                                             \
  template <> polyglot_array<CTYPE>::~polyglot_array() {                       \
    gnatpolyglot__ada__arrays__native__##ADATYPE##_array_free(this->_data);    \
  }                                                                            \
                                                                               \
  extern "C" void gnatpolyglot__ada__arrays__native__##ADATYPE##_array_copy(   \
      void *, array_data);                                                     \
  template <>                                                                  \
  polyglot_array<CTYPE>::polyglot_array(const polyglot_array<CTYPE> &other) {  \
    gnatpolyglot__ada__arrays__native__##ADATYPE##_array_copy(this,            \
                                                              other._data);    \
  }                                                                            \
                                                                               \
  template <>                                                                  \
  polyglot_array<CTYPE> &polyglot_array<CTYPE>::operator=(                     \
      const polyglot_array<CTYPE> &other) {                                    \
    gnatpolyglot__ada__arrays__native__##ADATYPE##_array_copy(this,            \
                                                              other._data);    \
    return *this;                                                              \
  }                                                                            \
                                                                               \
  extern "C" CTYPE *gnatpolyglot__ada__arrays__native__##ADATYPE##_array_get(  \
      array_data, int index);                                                  \
  template <> CTYPE &polyglot_array<CTYPE>::get(std::int32_t index) const {    \
    return *gnatpolyglot__ada__arrays__native__##ADATYPE##_array_get(          \
        this->_data, index);                                                   \
  }                                                                            \
                                                                               \
  extern "C" void gnatpolyglot__ada__arrays__native__##ADATYPE##_array_set(    \
      array_data, int, CTYPE);                                                 \
  template <>                                                                  \
  void polyglot_array<CTYPE>::set(std::int32_t index, const CTYPE &new_val) {  \
    gnatpolyglot__ada__arrays__native__##ADATYPE##_array_set(this->_data,      \
                                                             index, new_val);  \
  }

namespace gnatpolyglot::ada::arrays {

MAKE_ARRAY_FUNCTIONS(std::int8_t, short_short)
MAKE_ARRAY_FUNCTIONS(std::uint8_t, unsigned_short_short)
MAKE_ARRAY_FUNCTIONS(std::int16_t, short)
MAKE_ARRAY_FUNCTIONS(std::uint16_t, unsigned_short)
MAKE_ARRAY_FUNCTIONS(std::int32_t, int)
MAKE_ARRAY_FUNCTIONS(std::uint32_t, unsigned_int)
MAKE_ARRAY_FUNCTIONS(std::int64_t, long)
MAKE_ARRAY_FUNCTIONS(std::uint64_t, unsigned_long)

} // namespace gnatpolyglot::ada::arrays
