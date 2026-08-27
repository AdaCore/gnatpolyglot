//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef GNATPOLYGLOT_ADA_STRINGS_H
#define GNATPOLYGLOT_ADA_STRINGS_H

#include "gnatpolyglot_ada_arrays.h"

#if __has_include(<string>)
   #include <string>
#else
   #include <new>
#endif

namespace gnatpolyglot::ada::strings {

typedef arrays::array_data string_data ;

class polyglot_string {
public:
    class view;

public:
    polyglot_string(string_data data) :_data(data) {}
    polyglot_string(const char* str);
    ~polyglot_string();

    char &at(std::int32_t index);
    void set(std::int32_t index, char new_val);

    char &operator [](std::int32_t index);
    char operator [](std::int32_t index) const;

    int get_begin() const { return _data.begin; }
    int get_end() const { return _data.end; }

    int size() const {
        return this->get_end() - this->get_begin() + 1;
    }

    string_data data_() const { return this->_data; }
    string_data release_() const {
        return this->_data;
    }
    string_data release_() {
        string_data data = this->_data;
        this->_data.begin = 0;
        this->_data.end = 0;
        this->_data.data = nullptr;
        return data;
    }

    template <typename T>
    friend class gnatpolyglot::polyglot_ptr;

private:
    string_data _data;

    bool is_shadow() const {
        return false;
    }

    void set_owner(gnatpolyglot::memory_owner) {
        // NOOP
    }

};

class polyglot_string::view  {
private:
    view(const string_data &data) {
        new (static_cast<char*>(this->_data)) polyglot_string(data);
    }

public:
    static inline view create(const string_data &data) {
        return view(data);
    }

    polyglot_string& operator*() {
        return *(polyglot_string*) &_data;
    }

    const polyglot_string& operator*() const {
        return *(polyglot_string*) &_data;
    }

    operator polyglot_string&() {
        return *(polyglot_string*) &_data;
    }

    operator const polyglot_string&() const {
        return *(polyglot_string*) &_data;
    }

    polyglot_string* operator->() {
        return (polyglot_string*) &_data;
    }

    const polyglot_string* operator->() const {
        return (polyglot_string*) &_data;
    }

private:
    char _data[sizeof(polyglot_string)];
};

#if __has_include(<string>)

/** Return a new string whose characters are the conversion of each element from arr. */
polyglot_string from_string(const std::string &str);

/** Return a new string whose characters are the conversion of each element from arr. */
std::string to_string(const polyglot_string &arr);

#endif

} // namespace gnatpolyglot::ada::strings

#endif /* ! GNATPOLYGLOT_ADA_STRINGS_H */
