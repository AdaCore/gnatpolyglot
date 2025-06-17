#ifndef POLYGLOT_ADA_STRINGS_H
#define POLYGLOT_ADA_STRINGS_H

#include "polyglot_ada_arrays.h"

#include <string>

namespace polyglot::ada::strings {

typedef arrays::array_data string_data ;

class polyglot_string {
public:
    class view {
    public:
        view(const string_data &data) : _data(data) {}

        operator polyglot_string&() {
            return *(polyglot_string*)this;
        }

        polyglot_string* operator->() {
            return (polyglot_string*)this;
        }

    private:
        string_data _data;
    };

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


    string_data data() const { return this->_data; }

private:
    string_data _data;
};

/** Return a new string whose characters are the conversion of each element from arr. */
polyglot_string from_string(const std::string &str);

/** Return a new string whose characters are the conversion of each element from arr. */
std::string to_string(const polyglot_string &arr);

} // namespace polyglot::ada::strings

#endif /* ! POLYGLOT_ADA_STRINGS_H */
