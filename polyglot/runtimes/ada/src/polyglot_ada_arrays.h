#ifndef POLYGLOT_ADA_ARRAYS_H
#define POLYGLOT_ADA_ARRAYS_H

#include <cstddef>
#include <cstdint>
#include <iostream>
#include <type_traits>
#include <polyglot_ptr.h>

namespace polyglot::ada::arrays {

extern "C" struct array_data {
  int begin;
  int end;
  void *data;
};

static_assert(std::is_standard_layout<array_data>::value,
              "array_data type is not C compatible");

// Helper to get references or view types if T is scalar or not.
template <typename T, bool = std::is_scalar<T>::value> struct ref_selector;

// Specialization for scalar types: R is T&
template <typename T> struct ref_selector<T, true> {
  using type = T &;
};

// Specialization for non-scalar types: R is T::ref
template <typename T> struct ref_selector<T, false> {
  using type = typename T::view;
};

// Specialization for pointer types: pointers are not returned by reference.
template <typename T> struct ref_selector<polyglot_ptr<T>, false> {
  using type = polyglot_ptr<T>;
};

template <typename T> class polyglot_array {
public:
    class view {
    private:
        view(const array_data &data) {
            new (&this->_data) polyglot_array<T>(data);
        }

    public:
        static inline view create(const array_data &data) {
            return view(data);
        }

        operator polyglot_array<T>&() {
            return *(polyglot_array<T>*) &_data;
        }

        operator const polyglot_array<T>&() const {
            return *(polyglot_array<T>*) &_data;
        }

        polyglot_array<T>& operator*() {
            return *(polyglot_array<T>*) &_data;
        }

        const polyglot_array<T>& operator*() const {
            return *(polyglot_array<T>*) &_data;
        }

        polyglot_array<T>* operator->() {
            return (polyglot_array<T>*) &_data;
        }

        const polyglot_array<T>* operator->() const {
            return (polyglot_array<T>*) &_data;
        }

    private:
        char _data[sizeof(polyglot_array<T>)];
    };

public:
    polyglot_array(array_data data) :_data(data) {}
    polyglot_array(int begin, int end);
    ~polyglot_array();

    polyglot_array(const polyglot_array<T> &other);
    polyglot_array &operator=(const polyglot_array<T> &other);

    // Define R: if T is scalar, R is T&; otherwise, R is T::view
    using R = typename ref_selector<T>::type;

    R get(std::int32_t index) const;
    void set(std::int32_t index, const T &new_val);

    int get_begin() const { return this->_data.begin; }

    int get_end() const { return this->_data.end; }

    int size() const {
        return this->get_end() - this->get_begin() + 1;
    }

    /** Internal use only */
    array_data data() const { return this->_data; }

    /** Internal use only */
    array_data release() {
        array_data data = this->_data;
        this->_data.begin = 0;
        this->_data.end = 0;
        this->_data.data = nullptr;
        return data;
    }

    friend polyglot_ptr<polyglot_array<T>>;

private:
    array_data _data;

    bool is_shadow() const {
        return false;
    }
};

} // namespace polyglot::ada::arrays

#endif /* ! POLYGLOT_ADA_ARRAYS_H */
