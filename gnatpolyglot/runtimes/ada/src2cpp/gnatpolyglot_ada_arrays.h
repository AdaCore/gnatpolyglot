//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

#ifndef GNATPOLYGLOT_ADA_ARRAYS_H
#define GNATPOLYGLOT_ADA_ARRAYS_H

#include <cstddef>
#include <cstdint>
#include <type_traits>
#include <gnatpolyglot_ptr.h>

namespace gnatpolyglot::ada::arrays {

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
  using iterator_type = T *;
};

// Specialization for non-scalar types: R is T::ref
template <typename T> struct ref_selector<T, false> {
  using type = typename T::view;
  using iterator_type = type;
};

// Specialization for pointer types: pointers are not returned by reference.
template <typename T> struct ref_selector<polyglot_ptr<T>, false> {
  using type = polyglot_ptr<T>;
};

template <typename T> class polyglot_array {
public:

    // Define R: if T is scalar, R is T&; otherwise, R is T::view
    using R = typename ref_selector<T>::type;

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

    struct iterator {
    private:
        iterator(polyglot_array<T> &ref, int index)
            : _ref(ref)
            , _index(index)
            , _view(nullptr)
            , _view_initialized(false)
        {}

        using view_hold = typename ref_selector<T>::iterator_type;

        view_hold to_hold(R view);

        void prepare_view() {
            if (!_view_initialized) {
                _view = to_hold(_ref.get(_index));
                _view_initialized = true;
            }
        }

    public:
        bool operator==(const iterator &other) {
            return _ref._data.data == other._ref._data.data
                && this->_index == other._index;
        }

        bool operator!=(const iterator &other) {
            return !(*this == other);
        }

        T &operator*() {
            prepare_view();
            return *_view;
        }

        T *operator->() {
            prepare_view();
            return _view.operator->();
        }

        iterator &operator++() {
            _index ++;
            _view_initialized = false;
            return *this;
        }

        iterator operator++(int) {
            return iterator(_ref, _index + 1);
        }

        friend class polyglot_array<T>;

    private:
        polyglot_array<T> &_ref;
        int _index;
        view_hold _view;
        bool _view_initialized;
    };

public:
    polyglot_array(array_data data) :_data(data) {}
    polyglot_array(int begin, int end);
    ~polyglot_array();

    polyglot_array(const polyglot_array<T> &other);
    polyglot_array &operator=(const polyglot_array<T> &other);

    R get(std::int32_t index) const;
    void set(std::int32_t index, const T &new_val);

    iterator begin() {
        return iterator(*this, get_begin());
    };

    iterator end() {
        return iterator(*this, get_end() + 1);
    };

    int get_begin() const { return this->_data.begin; }

    int get_end() const { return this->_data.end; }

    int size() const {
        return this->get_end() - this->get_begin() + 1;
    }

    /** Internal use only */
    array_data data_() const { return this->_data; }

    /** Internal use only */
    array_data release_() {
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

template<typename T>
inline typename polyglot_array<T>::iterator::view_hold
polyglot_array<T>::iterator::to_hold(polyglot_array<T>::R view) {
    return view;
}

template<>
inline char *polyglot_array<char>::iterator::to_hold(char &view) {
    return &view;
}

template<>
inline unsigned char *
polyglot_array<unsigned char>::iterator::to_hold(unsigned char &view) {
    return &view;
}

template<>
inline short *polyglot_array<short>::iterator::to_hold(short &view) {
    return &view;
}

template<>
inline unsigned short *
polyglot_array<unsigned short>::iterator::to_hold(unsigned short &view) {
    return &view;
}

template<>
inline int *polyglot_array<int>::iterator::to_hold(int &view) {
    return &view;
}

template<>
inline unsigned int *
polyglot_array<unsigned int>::iterator::to_hold(unsigned int &view) {
    return &view;
}

template<>
inline long *polyglot_array<long>::iterator::to_hold(long &view) {
    return &view;
}

template<>
inline unsigned long *
polyglot_array<unsigned long>::iterator::to_hold(unsigned long &view) {
    return &view;
}

} // namespace gnatpolyglot::ada::arrays

#endif /* ! GNATPOLYGLOT_ADA_ARRAYS_H */
