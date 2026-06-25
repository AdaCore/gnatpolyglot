//
//  Copyright (C) 2025-2026, AdaCore
//  SPDX-License-Identifier: Apache-2.0
//

//! Ada->Rust bridge runtime that generated Rust bindings link against. It is the
//! Rust counterpart of the C++ bridge under `runtimes/ada/src2cpp`: it lives
//! under the Ada runtime because it binds the Ada runtime's own FFI symbols
//! (`gnatpolyglot__ada__arrays__*`, `gnatpolyglot__ada__strings__*`), holding
//! the shared FFI vocabulary so the generated crate stays thin.
//!
//! The support types are exposed under the [`ada`] module tree
//! (`ada::arrays`, `ada::strings`), mirroring the C++ `gnatpolyglot::ada::arrays`
//! / `gnatpolyglot::ada::strings` namespaces: the input language lives in the
//! module path, not in the type names, so client code that `use`s the module
//! never has to spell out "Ada" again.

pub mod ada;
