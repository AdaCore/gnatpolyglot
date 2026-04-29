# proxy2rust — Implementation Plan

A Rust binding generator following the same two-layer pattern as `proxy2cpp` and `proxy2java`:
raw `ffi` module with `extern "C"` declarations + safe public API on top.

**Status:** Phases 1–3 complete (Maven module skeleton, free functions, enumerations, non-virtual
classes with constructors / getters / setters / methods / Drop / Clone).
Phase 4 in progress: Deref chain for inheritance done (test 0007 passes); vtable/subclassing deferred.

---

## Module structure (Maven)

Sub-module `gnatpolyglot/proxy2rust/` alongside proxy2cpp and proxy2java:

```
proxy2rust/
├── pom.xml
└── src/main/
    ├── java/com/adacore/gnatpolyglot/proxy2rust/
    │   ├── Proxy2Rust.java          # picocli entry point
    │   ├── RustPrinter.java         # orchestrator
    │   ├── RustAPI.java             # type name generation + conversion helpers
    │   └── helpers/
    │       ├── TypenameGenerator.java
    │       ├── ParameterConverter.java
    │       └── ReturnConverter.java
    └── jte/
        ├── cargo_toml.jte
        ├── build_rs.jte
        ├── lib_rs.jte
        ├── module.jte
        ├── class.jte
        ├── enum.jte
        └── function.jte
```

## Generated output layout

```
<output_dir>/
├── Cargo.toml
├── build.rs
└── src/
    ├── lib.rs               # pub mod declarations
    └── <module_name>.rs     # per Ada package: ffi block + safe wrappers
```

For classes and enums (phases 4–5), the layout becomes per-module subdirectories:

```
src/
├── lib.rs
└── <module>/
    ├── mod.rs
    ├── <ClassName>.rs
    └── <EnumName>.rs
```

## Ada feature → Rust mapping

| Ada / Proxy IR | Rust |
|---|---|
| Free function | `pub fn` in module |
| `EnumerationDecl` | `#[repr(i32)] enum Foo { A=0, B=1 }` |
| `ClassDecl` (non-virtual) | `struct Foo(NonNull<c_void>)` + `impl` |
| `ClassDecl` (VIRTUAL inheritability) | struct + `trait FooMethods` |
| Inheritance (`parent` field) | `Deref<Target=Parent>` |
| METHOD role | `impl Foo { pub fn method(&self) }` |
| CONSTRUCT role | `impl Foo { pub fn new(...) -> Foo }` |
| DESTRUCT role | `impl Drop for Foo` |
| COPY role | `impl Clone for Foo` |
| `isConst` on first param | `&self` vs `&mut self` |
| `PointerTypeExpr` (isNonNull) | `NonNull<T>` in FFI, `&mut T` in safe layer |
| `PointerTypeExpr` (!isNonNull) | `Option<NonNull<T>>` |
| `ArrayTypeExpr` | `AdaArray<T>` (runtime type) |
| `STRING` native | `AdaString` / `&AdaStr` (runtime type) |
| `ExceptionDecl` | `enum Error` variant; functions return `Result<T, E>` |
| `Owner::USER` return | wrapper struct calling free on `Drop` |
| `Owner::LIBRARY` return | wrapper struct with no `Drop` |
| `Owner::STATIC` return | plain ref with `'static` lifetime |

## Runtime crate

New `runtimes/proxy2rust/` (Apache-2.0, mirrors `runtimes/proxy2java/`):

```
runtimes/proxy2rust/
├── Cargo.toml
└── src/
    ├── lib.rs
    ├── string.rs    # AdaString / AdaStr wrapping polyglot_string
    ├── array.rs     # AdaArray<T>
    └── error.rs     # AdaException, thread-local error slot
```

## Test infrastructure

Test baselines live under `testsuite/tests/ada/<test>/rust/` (mirrors `cpp/` and `java/` dirs).
Each contains `test.yaml`, `test.out`, and `main.rs` (usage example).

`utils.py` routes `output_lang: rust` through `cargo build --quiet`, passing the Ada library
location via `POLYGLOT_PROXY_LIB_DIR` / `POLYGLOT_PROXY_LIB_NAME` env vars read by the generated
`build.rs`.

## Phased implementation

| Phase | Deliverable | Status |
|---|---|---|
| 1 | Maven module, CLI entry point, free functions, `Cargo.toml` + `build.rs`, test infra | ✅ Done |
| 2 | Enumerations | ✅ Done |
| 3 | Structs (non-virtual classes) with constructors, methods, `Drop` | ✅ Done |
| 4 | Inheritance (`Deref` chain), virtual classes + trait generation | 🔄 Deref done (0007 ✅); vtable/subclassing pending |
| 5 | Exception → `Result` mapping | |
| 6 | Runtime crate (`AdaString`, `AdaArray`), string + array types | |
| 7 | Baselines for remaining existing `ada/` tests | |

## Open questions

1. **Trait vs. Deref for inheritance** — `Deref` is simpler to generate but produces
   `#[warn(deref_on_ref)]` noise; decide upfront before phase 4.
2. **Cargo workspace vs. standalone crate** — Relevant if an Ada project has multiple libraries.
3. **String representation** — Ada strings can contain null bytes; `AdaStr` may need to be
   `[u8]`-backed rather than `str`-backed.
4. **Exception thread-local slot** — `std::thread_local!` needs care in `async` contexts;
   worth documenting as a limitation.
