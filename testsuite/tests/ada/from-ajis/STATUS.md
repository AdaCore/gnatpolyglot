# AJIS → gnatpolyglot conversion status

This file tracks all AJIS tests that have been surveyed for conversion into the
gnatpolyglot testsuite (under `testsuite/tests/ada/from-ajis/`).  For each test
we record a brief description, the Ada constructs exercised, and the per-printer
conversion status.

Each converted test produces up to two sub-tests: a **java/** variant that
exercises the Java printer and a **cpp/** variant that exercises the C++ printer.
The **cpp** and **java** columns reflect per-printer status.  For tests that have not been
imported, both columns carry the same marker.

## Summary

203 AJIS tests imported. Results on Linux x86-64 with the implementation from
`origin/main` at `232a2b87`:

| Variant | ✓ | XFAIL |
|---|---:|---:|
| Ada scanner (203) | 190 | 13 |
| C++ (203) | 136 | 67 |
| Java (203) | 135 | 68 |

Not imported: 18 open questions, 1 duplicate, plus the tests without a Java
file listed at the end.

## Status legend

| Symbol | Meaning |
|--------|---------|
| ✓ | Sub-test imported and passing with its GNATpolyglot baseline |
| XFAIL | Sub-test imported but marked `XFAIL`: the reason (limitation, bug with its `ajis-bugs/` reproducer, or open question) is in the Notes and in `test.yaml` |
| — | Sub-test not created for this printer |
| Question | Not imported: open question on how to port AJIS-specific behaviour |
| Duplicate | Not imported: duplicate of another test |
| Candidate | Not yet looked at |
| No test | No Java file in the original AJIS test; nothing to import |

AJIS-specific `pragma Annotate (AJIS, …)` directives are **ignored** when
assessing convertibility: they are metadata for the AJIS binding generator and
carry no meaning for gnatpolyglot.

---

## H-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| H213-011 | Simple integer function `Addition(A, B)` | ✓ | ✓ |  |
| H222-020 | Discriminated record with default discriminant | ✓ | ✓ | `new Rec (1234)` ported as `new Rec (1234, 0, 0, 0)`: the constructor takes all components |
| H514-005 | Simple procedure with output | ✓ | ✓ |  |
| H514-006 | Procedure with `String` parameter | ✓ | ✓ |  |
| H519-004 | Function returning `String` | ✓ | ✓ |  |
| H519-005 | `access` parameter to `String` | XFAIL | XFAIL | XFAIL: bug: String_Access passed back to Ada loses its identity (ajis-bugs/string-access-identity) |
| H519-006 | Anonymous access parameter | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| H519-007 | Access type + private record (two Java files) | ✓ | ✓ | Both ports cover cases 1 and 3: `Set_Fields`/`Display_My_Type` methods and named-access procedures. Case 2 (`Set_Fields_2`/`Display_My_Type_2`) is excluded because anonymous access types are not supported. Java temporarily transfers ownership for named-access calls. |
| H519-008 | Simple access type declaration | ✓ | ✓ | Both ports pass; Java uses `PolyglotString` values and unwraps the non-null string access getters. |
| H519-009 | Access type as record field | ✓ | ✓ | Both ports pass; Java uses `PolyglotString` values and unwraps the non-null string access getters. |
| H519-010 | Access to enum-indexed array | ✓ | ✓ | open question: the passing variants test F()/Sum rather than the AJIS An_Array operations; An_Enum_Array not bindable (enum index) |
| H520-022 | Array of `String` access values | XFAIL | XFAIL | XFAIL: limitation: arrays of arrays / of access to arrays are not yet supported |
| H520-023 | Access-to-function pointer | ✓ | ✓ |  |
| H520-024 | Access-to-function with record parameter | ✓ | ✓ |  |
| H520-025 | Access in record (self-referential structure) | ✓ | ✓ |  |
| H520-026 | Access-to-procedure type | ✓ | ✓ |  |
| H520-027 | Procedure with enum-indexed array | XFAIL | XFAIL | XFAIL: limitation: arrays indexed by enumeration types are not bindable |
| H520-028 | Two overloaded `F` functions by return type + AJIS `Rename` pragma | ✓ | ✓ | Overloaded functions disambiguated as `standard_integer_f` and `test_r_f` |
| H520-029 | Two overloaded `F` by record param type + `Print`/`Get_Std_Output` using `File_Type` | ✓ | ✓ |  |
| H520-030 | Discriminated private type + array | ✓ | ✓ |  |
| H520-031 | Enum + `out`/`in out` for Integer, Boolean, Float, Enum | ✓ | ✓ |  |
| H522-003 | Basic tagged type with primitive procedure | ✓ | ✓ | C++ subclass overrides P_1 to test dispatch through Call_P1 |
| H522-004 | Tagged type with `access T'Class` parameter and return type | ✓ | ✓ |  |
| H522-005 | Tagged type with class-wide access return type | ✓ | ✓ |  |
| H522-006 | Abstract tagged type + abstract procedure | XFAIL | XFAIL | XFAIL: bug: class-wide result of a hidden descendant of an abstract root is null (ajis-bugs/hidden-descendant-abstract-root); the C++/Java subclasses override `P_1` to test dispatch through `Display_P1` (the C++ one prints `CppChild`) |
| H522-007 | Abstract private tagged types | ✓ | ✓ | C++ subclasses override P using shadow constructor pattern |
| H522-008 | Multiple packages with `with`/`use` | XFAIL | XFAIL | XFAIL: bug: "=" called in the subtype's package (ajis-bugs/proxy-equality-wrong-package); limitation: derived access types are not supported; input XFAIL: bug: "=" called in the subtype's package (ajis-bugs/proxy-equality-wrong-package); limitation: derived access types are not supported |
| H522-009 | Multiple packages + access types (renamed packages) | ✓ | ✓ |  |
| H522-010 | `access abstract T` + abstract function | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| H522-011 | Access-to-procedure parameters | ✓ | ✓ |  |
| H522-012 | Multiple packages + child unit + `String_Access` return type | ✓ | ✓ |  |
| H523-006 | Tagged type + `access T` parameter | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| H523-007 | Enum + access-to-procedure | ✓ | ✓ |  |
| H523-008 | Multiple packages + global `Rec_Access` variable | ✓ | ✓ |  |
| H523-010 | Unconstrained private discriminated type `(<>)` | ✓ | ✓ | Scanner passes; discriminant hidden from public interface, treated as opaque |
| H523-011 | Three packages + record hierarchy | ✓ | ✓ |  |
| H523-012 | Multiple packages + tagged + three access variants | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (P_2) |
| H523-013 | Tagged type with `T'Class` return | XFAIL | XFAIL | XFAIL: bug: proxy calls primitives with two controlling operands statically: dispatch lost (ajis-bugs/multiple-controlling-params-static-call): both ports print `r2 = 3` instead of `r2 = 30` |
| H523-014 | `access constant` type | ✓ | ✓ |  |
| H523-015 | Tagged `limited` record | ✓ | ✓ |  |
| H611-004 | Exception raise + access-to-procedure + AJIS pragma | ✓ | ✓ |  |
| H624-010 | Access-to-procedure type + null check + callback | XFAIL | XFAIL | XFAIL: bug: null access-to-subprogram not handled (ajis-bugs/null-access-to-subprogram) |
| H624-011 | Tagged + `access T'Class` + Boolean param | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (Set_X) |
| H624-012 | Controlled + tagged + access | XFAIL | XFAIL | XFAIL: open question: library-owned controlled objects are finalized at exit (UNEXPECTED FREE), unlike AJIS |
| H624-013 | Record + access type + callback + tagged | ✓ | ✓ |  |
| H624-014 | Enum + `pragma Pack` record + array | XFAIL | XFAIL | Both ports check the packed record fields, array elements, and global fields. The generated getters return incorrect enum values in C++ and Java maps invalid values to `null` (ajis-bugs/packed-record-component-getters). |
| H624-015 | Simple tagged record | ✓ | ✓ | No .adb needed; test sets/reads global tagged record fields |
| H624-016 | Exception raise + access-to-procedure | ✓ | ✓ | C++ tests exception catch |
| H624-017 | Function that raises an exception | ✓ | ✓ |  |
| H624-018 | Scalar range subtype + record | XFAIL | XFAIL | XFAIL: missing feature: Ada `"="` not mapped to `equals ()`/`operator==` (ajis-bugs/equality-operator-not-mapped); `Int_R`/`Int_R_Tagged` map to `int8_t`/`byte`; the C++ port compares the components |
| H624-019 | Simple enum + Boolean function | ✓ | ✓ |  |
| H626-013 | `access String` + create/print utilities | ✓ | ✓ | C++ tests null and non-null `String_Access` |
| H626-014 | Record + access-to-function + tagged | ✓ | ✓ | C++ tests `Rec_Tagged.F` (String return method) |
| H626-015 | Record + array access + null check | ✓ | ✓ |  |
| H626-016 | `access String` with `out`/`in out` parameters | ✓ | ✓ | `Create_String`/`Create_And_Append_String` use `out`/`in out` `String_Access` params |
| H626-017 | Function returning `access String` | ✓ | ✓ |  |
| H626-018 | Controlled type (`Adjust`/`Initialize`/`Finalize`) where `Finalize` raises `Program_Error` | XFAIL | XFAIL | XFAIL: bug: exception raised by Finalize kills the process (ajis-bugs/finalize-exception-default-free); Ada exception in C++ destructor causes recursive `std::terminate` |
| H626-019 | Abstract tagged + class-wide dispatch | ✓ | ✓ |  |
| H626-020 | Tagged `limited` type + inheritance | ✓ | ✓ |  |
| H626-021 | Tagged + `access T'Class` + access-to-function | ✓ | ✓ |  |
| H626-022 | Complex tagged record + procedure | ✓ | ✓ | Print_Vals emitted as method on ExtractLine (first non-tagged param) |
| H627-006 | Generic package + procedure | ✓ | ✓ | the generic declaration `Gen.Something` is not bound (warning), its instantiation `Gen.P` is |
| H627-007 | Tagged inheritance A → B with overridden functions | ✓ | ✓ |  |
| H627-008 | Generic base package + instantiation | ✓ | ✓ | the generic declaration `Base` is not bound (warning), its instantiation `Base_Instance` is |
| H627-009 | Abstract tagged private type + cross-package return type | ✓ | ✓ | C++ shadow subclass overrides `get_something` |
| H627-011 | Multiple packages + private tagged + access | ✓ | ✓ | the constructor also takes the initial `Loc_Str` (three spaces, then set to "123") |
| H627-012 | Abstract tagged `limited` + concrete child type | ✓ | ✓ |  |
| H627-013 | Exception propagation through abstract dispatch | ✓ | ✓ | C++ shadow override throws, Ada `when others` catches/re-raises, C++ catches re-raised |
| H627-014 | Multiple packages + controlled inheritance | ✓ | ✓ | Both ports cover native controlled cleanup and host subclass cleanup through the generated base. Java extends the Cleaner action via `_getFree()` and calls final `close()` deterministically; C++ uses virtual destruction at scope exit. GC scheduling and Java `finalize()` are excluded. |
| H627-015 | Multiple packages + private tagged type | ✓ | ✓ | C++ tests `A_Proc` (method on `AType`) and `Call_A_Proc` (free function) |
| H709-002 | Multiple packages + private tagged inheritance | ✓ | ✓ | C++ tests `Proc` (method on tagged `ARecord`) |
| H709-003 | Multiple packages + child unit + range return type | ✓ | ✓ | `Test.Child.F` returns `Base.ID` (range 1..10 → `int8_t`); both printers pass |
| H709-004 | Basic procedure | ✓ | ✓ |  |
| H709-005 | Simple procedure | ✓ | ✓ |  |
| H709-006 | Controlled type (`Initialize`/`Finalize` triggered on construction/destruction) | ✓ | ✓ |  |
| H709-007 | Private tagged type with discriminant | XFAIL | XFAIL | XFAIL: bug: the getter of the discriminant `I` and the bound function `Get_I` are both `get_i ()`/`getI ()`, which does not compile (ajis-bugs/disc-getter-clashes-function) |
| H709-008 | Multiple packages + AJIS `Rename`/`Attached` pragmas | ✓ | ✓ | Both ports construct `R(999)` and call `Proc_With_Owned_Allowed`/`Proc_With_Owned_Not_Allowed`. Java temporarily transfers ownership for access parameters. AJIS-specific `Assume_Escaped` ownership exceptions are excluded. |
| H709-009 | Tagged type inheritance + overloaded `F` by return type + AJIS annotations | ✓ | ✓ | Overloaded F disambiguated per level: `f`/`f_1` (T1), `f`/`f_2` (T2), `f`/`f_3` (T3) |
| H709-011 | Ada task with entry + AJIS `Locking` pragma | Question | Question | the test checks the AJIS `Locking` annotations (`Check_Only` must raise unless the global `Library.lock` is held), with no gnatpolyglot equivalent; its task part (a Java/C++ thread releasing an Ada task another thread waits on) works |
| H715-006 | Array + record + access | ✓ | ✓ | C++ tests `get_v()` (global access-type var) record field set/get |
| H715-007 | Access tagged type + multiple procedures | ✓ | ✓ |  |
| H715-008 | Simple record + procedure | ✓ | ✓ |  |
| H715-009 | Access tagged type function | XFAIL | ✓ | C++ XFAIL: open question: internal_clone_ + copy constructor as a stand-in for AJIS proxyCloned |
| H715-010 | Three global integer variables | ✓ | ✓ |  |
| H715-011 | Ada task type + protected type | ✓ | ✓ | the tasks and protected type of the spec are not bound ("not supported" warnings), and the test does not use them |
| H715-013 | Access-to-function procedure | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| H715-014 | Access tagged function/procedure | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| H715-015 | Global constrained + unconstrained array variables | XFAIL | XFAIL | Limitation: constrained array type declarations and dependent declarations are skipped by the scanner. |
| H818-014 | Abstract `Type_1`/`Type_2` + `Get` returns `Type_1'Class` + `Create_And_Free` + `Acc_Type_1` | XFAIL | XFAIL | C++ XFAIL: open question: finalize lines differ from AJIS (GC-driven); bug: USER-owned result of an override is freed too early (ajis-bugs/cpp-override-user-owned-return); Java XFAIL: bug: Ada cannot copy the result of a Java override returning T'Class (ajis-bugs/java-override-classwide-return-clone); AJIS test was primarily testing AJIS-specific GC/ownership lifecycle |
| H821-007 | Tagged + non-tagged types, access-to-classwide, access-to-subprogram callbacks | XFAIL | XFAIL | XFAIL: open question: AJIS ownership values and identity of host objects passed through Ada differ; AJIS test was entirely about AJIS-specific ownership semantics (no Ada `Put_Line` output) |
| H826-009 | Exception-raise procedure (handled internally) | ✓ | ✓ |  |
| H828-006 | Generics + multiple packages + inheritance | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (`Primitive (V : access Root_Type)`); the instantiation `I` is bound |
| H829-011 | Limited record + nested record global variable | ✓ | ✓ |  |
| H829-012 | Null record + two procedures | ✓ | ✓ |  |
| H829-013 | Tagged type + procedure | ✓ | ✓ | P(T'Class) → free function; P1(in out T) → method; Q1/Q2 non-controlling → free functions |
| H829-014 | Integer functions (`Integer'Last`, `Long_Long_Integer'Last`) + `in out` procedures | ✓ | ✓ | cpp: `Long_Long_Integer` → `int64_t` (≡ `long`) conflicts with `long long` in test |
| H829-015 | Access-to-procedure + AJIS annotations | XFAIL | ✓ | Java covers the supported calls. AJIS escape checks (Assume_Escaped) have no equivalent. The generated C++ setter for a global access-to-subprogram does not compile (ajis-bugs/cpp-global-access-to-subprogram-setter). |
| H829-016 | Access-to-procedure | XFAIL | XFAIL | XFAIL: bug: host exception thrown in a callback is replaced by a generic one (ajis-bugs/host-exception-through-callback) |
| H829-017 | Access-to-procedure | ✓ | ✓ | C++ tests `A` record field get/set and global `G1`/`G2` mutation via pointer |
| H829-018 | Tagged type with P(T), P(access T), P(T'Class), P(access T'Class) | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (`P2`, `P4`) |
| H829-019 | Two null procedures, AJIS `Lock` pragma | ✓ | ✓ | procedures do nothing, test verifies binding compiles and runs (the AJIS `lock`/`unlock` output comes from its `Lock` pragma) |
| H829-020 | Access tagged + AJIS annotations | ✓ | ✓ |  |
| H901-005 | Private type + AJIS `Attached` pragma | ✓ | ✓ | `T` (private null record) and `P(V : S)` (subtype = T) both bound and tested |
| H902-026 | Tagged type with discriminant `(V : Natural)` | ✓ | ✓ |  |
| H902-029 | Tagged + discriminated `(V : Integer)` record | ✓ | ✓ |  |
| H903-007 | Multiple packages + subtypes | ✓ | ✓ | `P_B.A_Subtype` resolves to `P_A.A_Type` (null record); `P_B.An_Other_Subtype` resolves to `P_Other_Type.An_Integer` (range 1..10 → `int8_t`/`byte`) |
| H903-021 | Access-to-procedure + AJIS `Assume_Escaped` | ✓ | ✓ |  |
| H904-011 | Enum + enum-indexed character arrays + AJIS annotations | XFAIL | XFAIL | XFAIL: limitation: arrays indexed by enumeration types are not bindable |
| H904-034 | Access-to-procedure type + `Assume_Escaped` pragma + exception bridging | XFAIL | XFAIL | The original Ada handler expects AJIS-style host exception details in `Exception_Information`, including the bracketed metadata added by the AJIS bridge. GNATpolyglot does not yet provide equivalent details (ajis-bugs/host-exception-through-callback). |
| H909-008 | Integer arrays + access-to-array + global vars + record with array fields | ✓ | ✓ | C++ covers all three parameter modes for all five storage forms and checks STATIC/LIBRARY pointer ownership; AJIS allocator queries/escape exceptions do not apply. Java uses `IntegerArray`, unwraps access getters, and temporarily transfers ownership for access parameters. |
| H910-025 | Record with `String(1..15)` field + array of records (`All_TNs_Type`) | XFAIL | XFAIL | Limitation: constrained array type declarations and dependent declarations are skipped by the scanner. |
| H918-002 | Array access (`access all An_Array`) + tagged `Root` with overridable `Get_An_Array` | ✓ | ✓ | C++ shadow subclass overrides `get_an_array` to return 43s |
| H918-005 | Controlled type (`Initialize`/`Adjust`/`Finalize`) + `Root_Array` + global `Finalized` counter | XFAIL | XFAIL | Both ports include host allocation and Ada-returned arrays. C++ hangs during host-array destruction; Java produces extra `Finalize` calls and can crash (ajis-bugs/array-of-controlled-free), plus a `System.runFinalization()` deprecation warning. |
| H918-007 | Custom integer range type + empty global array | ✓ | ✓ |  |
| H918-008 | `in out An_Array_Access` params + abstract tagged `Root` + `call_set_to_null_and_print` dispatch | ✓ | ✓ | C++ shadow subclass overrides abstract `set_to_null`; `memory_owner::LIBRARY` required for C++-allocated arrays |
| H919-008 | Controlled `Base_Record` with `Acc` pointer field + `A_Record` wrapper + `An_Array` (no free functions) | XFAIL | XFAIL | Both ports include host arrays, nested components, standalone records, and access after container finalization (ajis-bugs/array-of-controlled-free). C++ hangs during host-array destruction; borrowed component owners stay alive explicitly. Java retains the original parent-retention/GC checks and aborts after `FREE: 101`. |
| H923-006 | Private null record returned by value (access type ignored) | ✓ | XFAIL | `Get_Object` prints and returns null record (AJIS expects an exception, as it is called without the AJIS lock); Java XFAIL: bug: the generated `clone ()` returns an unqualified `Object`, i.e. the bound type `Test.Object` |
| HA09-014 | Enum-indexed array procedure | XFAIL | XFAIL | XFAIL: limitation: arrays indexed by enumeration types are not bindable |
| HA10-009 | `Print(String)` + `Modify(in out String)` | ✓ | ✓ |  |
| HA14-007 | Tagged type with `Fill(in out String)` method | ✓ | ✓ | C++ prints string values after mutation |
| HA14-008 | Tagged type + controlled private `Contained` + dispatch through `Call_Prim`/`Call_Prim2` | ✓ | ✓ | C++ shadow subclass overrides `prim`; `Adjust` fires on `Contained` copy into `Container` |
| HA14-012 | Access tagged + AJIS annotations: `Root` tagged type, `Str_Cst_Access`/`Str_Access` (access to String), `Print1`–`Print4`, `Get_Str` method | ✓ | XFAIL | Java unwraps the returned `PolyglotString` and exercises all four original calls; mutable parameters still accept access-to-constant strings instead of rejecting them. C++ checks rejection of `print2`/`print4` statically. |
| HA16-006 | Tagged type + `Root_Access` (access to classwide) + `F`/`Call_F`/`Create_Root` | XFAIL | XFAIL | C++ XFAIL: open question: AJIS allocator checks have no equivalent; Java XFAIL: open question: AJIS allocator checks have no equivalent; bug: access-to-classwide Ref written by Ada is STATIC (ajis-bugs/java-ref-owner-after-ada-write) |
| HA23-021 | Multiple packages + abstract access tagged | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| HB17-007 | Enum + access-to-procedure callback + AJIS annotations | ✓ | ✓ |  |
| HB17-009 | Access-to-procedure + AJIS annotations | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| HB17-010 | Discriminated tagged type with `String` fields | ✓ | ✓ | the constructors also take the initial `S1`/`S2` (blanks, then set); GNAT warns that the shadow back-reference converter will raise Constraint_Error (ajis-bugs/disc-shadow-back-ref-constraint-error), which does not happen in the test; C++ prints `FROM CPP` instead of `FROM JAVA` |
| HB21-009 | Access-to-function + AJIS annotations | ✓ | ✓ |  |
| HB21-013 | Access tagged type | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| HB25-008 | Enum array procedure | XFAIL | XFAIL | XFAIL: limitation: arrays indexed by enumeration types are not bindable |
| HB25-009 | Tagged type with procedure overloads | ✓ | ✓ | C++ shadow subclass overrides `p_1` to test dispatch through `call_p1` |
| HB28-010 | Two packages with same-named record type, global variables | ✓ | ✓ |  |
| HC03-014 | Array of strings (`Name_Types = array(1..5) of String(1..18)`) + global `Name_Register` | XFAIL | XFAIL | XFAIL: limitation: arrays of arrays / of access to arrays are not yet supported; C++ accesses `name_register()->get(2)` → "Bjarne Stroustrup "; package is `Society` (not `Test`) |
| HC04-011 | Enum access + AJIS annotations | ✓ | ✓ | C++ tests `Proc`/`Proc_2`/`Get` (enum params) |
| HC04-024 | Controlled inheritance + access-to-procedure + AJIS annotations | XFAIL | XFAIL | C++ XFAIL: bug: null access-to-record callback parameter seen as non-null (ajis-bugs/cpp-callback-null-access-record); open question: AJIS escape-check output; Java XFAIL: open question: AJIS escape-check output (port ready) |

---

## I-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| I115-002 | Access type + record + function | ✓ | ✓ | `F2` (takes `A2 = access all R`) bound and tested via `polyglot_ptr<R>` |
| I410-003 | Multiple packages + unconstrained array of tagged type + global | ✓ | ✓ | `File_Array = array(Positive range <>) of Virtual_File`; global `Ref.X(1..2)` accessed via `ref::x()`; `Prim` (method on `VirtualFile`) called with array arg |
| I410-006 | Array function (no `.adb`) | XFAIL | XFAIL | XFAIL: missing feature: subprogram renamings are not bound (ajis-bugs/subprogram-renaming-not-bound); F absent from binding |
| I410-011 | Ada child packages (VFS / VFS.Child / VFS.Sibling) | ✓ | ✓ | `get()` as method on `R` |
| I521-013 | Generic package + 5 Ada units + inheritance | XFAIL | XFAIL | XFAIL: bug: circular dependency between the proxy of a package and of its instantiation using one of its types (ajis-bugs/proxy-instantiation-circular-dependency); bug: objects declared in generic instantiations are not bound (`Gen_Inst.X`, ajis-bugs/proxy-instantiation-objects); `new R ()`/`new My_Rec ()` take initial component values |
| I522-006 | Global variable `V : Integer` | ✓ | ✓ |  |
| I522-007 | Nested package `package Nested is …` | ✓ | ✓ |  |
| I525-010 | Generics + multiple packages + AJIS annotations | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (`Add`); `Add2`, with a named access-to-subprogram, is bound; AJIS annotations removed |
| I525-011 | Nested generic package instantiation (`package Lists is new Ada.Containers.Doubly_Linked_Lists`) | XFAIL | XFAIL | XFAIL: bug: objects declared in generic instantiations are not bound (`No_Element`, ajis-bugs/proxy-instantiation-objects); missing feature: the predefined `"="` of `Cursor` is not mapped (AJIS compares cursors with `equals`) |
| I527-013 | Multiple packages + access-to-procedure + AJIS annotations | ✓ | ✓ |  |
| I527-014 | Constrained array subtype global variable (access type ignored) | ✓ | ✓ | gnatpolyglot silently skips it |
| I528-014 | Overloaded procedures + access type + tagged inheritance + AJIS annotations | ✓ | ✓ | overloads disambiguated by gnatpolyglot (`a`, `b`, `d`, `d_1`); C++ tests `a`/`b`/`t.c()`/`tc.c()`/`d`/`d_1` |
| I702-011 | Limited controlled private record + factory function | XFAIL | ✓ | C++ XFAIL: bug: returning a limited type by value requires C++17 (ajis-bugs/cpp-limited-return-by-value-cxx11); `Update_Lock` (limited tagged, inherits `Limited_Controlled`); `Lock_Updates` returns `Update_Lock` by value; C++ calls `unlock()` on returned lock |
| I717-024 | Simple procedure (package `P`) | ✓ | ✓ |  |
| I728-004 | Three overloaded `Value_Of` by parameter types | ✓ | ✓ | All three overloads bound as `value_of`, `value_of_1`, `value_of_2` |
| I915-006 | Child package `P.X` with null record + procedure + global | XFAIL | XFAIL | An object named like its enclosing package breaks proxy compilation in all three variants (ajis-bugs/proxy-global-hides-package). The anonymous access parameter of Proc_2 is also unsupported. |
| IC01-003 | Discriminated tagged type `Root_1(V: Integer)` + constrained child `Child_1_3` | XFAIL | XFAIL | XFAIL: bug: the shadow allocation of the private type `Child_1_3`, which constrains the discriminant of `Root_1`, gives the discriminant twice, so the proxy does not compile (ajis-bugs/disc-private-child-constraint-shadow) |

---

## J-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| J616-012 | Private tagged function with class-wide return | ✓ | ✓ |  |
| J617-004 | Abstract tagged type with abstract operator overloads (`=`, `+`, `-`, `*`, etc.) | XFAIL | XFAIL | XFAIL: proxy calls the abstract "=" statically on Test.Root views: call to abstract function must be dispatching (ajis-bugs/multiple-controlling-params-static-call); input XFAIL: proxy calls the abstract "=" statically on Test.Root views: call to abstract function must be dispatching (ajis-bugs/multiple-controlling-params-static-call) |
| J618-007 | `R_Array_2` (aliased-element unconstrained array) + access-to-array globals + access-to-procedure | ✓ | ✓ | Both ports preserve the array bounds after setting `V5`. |
| J622-006 | Abstract tagged `Error_Parser` + `Arr` (array of null record) + abstract `Initialize` | ✓ | ✓ | Scanner handles abstract tagged type with array parameter; empty main (no Ada API to call from outside) |
| J624-007 | Access to scalar types (`access Boolean`, `access Integer`) + named access to scalar | ✓ | ✓ | empty main — original AJIS test also had empty main; scanner warns appropriately |
| J624-013 | Abstract tagged `T` + concrete `T2` + `P(V1: R; V2: T2)` with non-controlling first parameter | XFAIL | XFAIL | XFAIL: bug: abstract primitive attached to its non-tagged first parameter type (ajis-bugs/abstract-primitive-non-controlling-first-param) |
| J624-022 | Generics + multiple packages | ✓ | ✓ | instantiation with a class-wide actual (`Root'Class`); the C++ `Child` overrides `internal_clone_`, as Ada copies the values given to `Set` |
| J624-025 | Abstract `Root` + abstract `Other_Root` + `P(Self: Root; V: not null access Other_Root'Class)` | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported |
| J624-032 | Abstract `Root` + concrete `Child` + two `F` overloads by return type (`Integer` / `Float`) | XFAIL | XFAIL | XFAIL: bug: return-type overloads get different names in each class (ajis-bugs/return-type-overload-names-hierarchy) |
| J628-003 | `T is new Integer` + abstract `"/"` + concrete `"*"` operator | ✓ | ✓ | `"*"` bound as `operator_mult`/`operatorMult`; returns 999 regardless of input |
| J901-015 | Abstract tagged `T` with `F1`/`F2`/`P` abstract + global `R` + `Test_All` + `Ptr_F`/`Ptr_P` access-to-subprogram | ✓ | XFAIL | C++ checks dispatch through `T'Class` using a shadow subclass. Porting the AJIS null and escape trace checks remains an open question. |
| J903-018 | Access-to-procedure + AJIS annotations (custom test) | Question | Question | AJIS tooling test (javac "does not override" diagnostic); not expressible in a proxy2print test |
| J903-022 | Access record function | Question | Question | whole AJIS output is --trace-null-conversions noise; port runs with empty output |
| J921-016 | Host subclass of tagged `R` overriding `Initialize (in out T; in out E)` (aliased Controlled component), dispatched via `Make_Calls (R'Class)` | ✓ | ✓ |  |
| JB26-018 | Access tagged function/procedure | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (`Clone_2`, `Call`, `Call2`); the baseline is the AJIS output, whose `NOT C` and three `CREATE JAVA C` reflect how AJIS copies objects: to review once the test can run |
---

## K-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| K323-015 | Inheritance + procedure | Question | Question | derived enum E3 (B .. C) has no A literal on the host side: Constraint_Error case not expressible in Java |
| K915-027 | Multiple packages + inheritance + array | XFAIL | XFAIL | XFAIL: bug: the 2-D array component `M` is bound as a 1-D array (see doc/limitations.rst, multi-dimensional arrays), and `Rec1` has no default constructor; the unrelated `test.ad[sb]` of the AJIS directory (not bound by its `test.cmd`) are not imported |
| K930-024 | Nested definite/indefinite vectors of tagged elements and a record containing both | XFAIL | XFAIL | Original Ada retained; Say main retained. Proxy compilation fails on circular dependencies between the parent and nested instantiation proxies (see I521-013). |
| KA19-018 | Generics + multiple packages | ✓ | ✓ | `Iterate`/`Reverse_Iterate` are not bound (anonymous access-to-subprogram), but not used by the test (AJIS did not bind `Reverse_Iterate` either) |
| KA20-073 | Simple procedure | ✓ | XFAIL | Java XFAIL: bug: the getter of the global `class` is a static `getClass()` clashing with `Object.getClass()` |
| KA30-002 | Tagged private type whose private part has a primitive with a null-record private parameter (binding generation only) | ✓ | ✓ | `expect-tty.ads` kept in src/ |

---

## L-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| L514-021 | Object renamings (constants, enum literals, renamings of renamings, child package) | ✓ | ✓ | C++ prints the enum literal name through a small table (C++ enums have no image) |
| LA17-010 | Procedure with `String` param + discriminated null record | ✓ | ✓ |  |
| LA22-008 | Multiple packages + controlled + exceptions | ✓ | ✓ |  |
| LB20-026 | Abstract tagged type derived from an abstract private tagged type with a null primitive; `Say (String)` | ✓ | ✓ |  |

---

## M-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| M201-019 | Multiple packages + access inheritance | ✓ | ✓ | the bug it was blocked by (descendant from another package identified without a with clause for it) is fixed |
| M315-039 | Enum array procedure (no `.adb`) | Question | Question | the test prints the `First`/`Last`/`Length` that AJIS derives from the bounds of array types created from the type alone (`new T3 ()`): native-element arrays are bound to runtime classes created with explicit bounds, so there is no equivalent; the scanner also crashes on the bound expression of `T10` (LangkitException) |
| M521-016 | Ada exception information and symbolic traceback from a host call | ✓ | ✓ | Original Ada retained, including the gnat_argv initialization workaround. Both mains check the original exception name/message and at test.adb:40. Proxy binder -E enables tracebacks; debug environment dumps are omitted. |
| M524-027 | Ada exception information and symbolic traceback without initializing gnat_argv | ✓ | ✓ | Original Ada retained, including the absence of the M521-016 initialization call. Both mains preserve the exception name/message and at test.adb:40 checks; proxy binder -E enables tracebacks. |
| M613-024 | Tagged record + function (no `.adb`) | ✓ | ✓ |  |
| M618-016 | Multiple packages + tagged + AJIS annotations | ✓ | ✓ | the bug it was blocked by (return-type overloads T / T'Class / Integer giving an invalid proxy) is fixed; AJIS annotations removed, and `config.ads` (AJIS annotations only, not bound by `test.cmd`) not imported |
| M702-014 | Expression function returning `String` | ✓ | ✓ | Output "OK" without trailing newline, as in AJIS |
| M702-023 | Abstract access inheritance (custom test script) | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (`F`, so `T` is final) |
| MC05-033 | Enum array procedure (no `.adb`) | Question | Question | the test prints the `First`/`Last`/`Length` that AJIS derives from the bounds of array types created from the type alone (`new T3 ()`): native-element arrays are bound to runtime classes created with explicit bounds, so there is no equivalent (here with empty ranges) |
---

## N-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| N217-045 | Tagged types `T1`/`T2` + `T2_Access` (access to classwide) + `Say(String)` + `P(in out T1; not null T2_Access)` | ✓ | ✓ | `Say` and T1/T2 bound |
| N304-036 | Private concrete descendant implementing a private override of an abstract primitive | XFAIL | XFAIL | Original base-typed dispatch check (PGI = 1961) retained. Generated descendant shadow declares PGI overriding where it is not visible; proxy does not compile. |
| N309-003 | Access tagged (custom test script) | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (the discriminant `O2` of `T2`); AJIS `setOwner (NATIVE)` ported as `_setOwner (LIBRARY)` |
| N312-031 | Component read through a class-wide aliased constant (`O_Class : aliased constant T'Class`) | ✓ | ✓ |  |
| N513-010 | Database record containing a classwide-interface map and a classwide constant | XFAIL | XFAIL | Original database-construction main retained with an explicit initial empty Map. Scanner/proxy pass; the classwide constant getter instantiates an abstract Java class and uses a missing C++ view. |
| N513-029 | Abstract primitive taking an access-to-function, overridden by a concrete type; called with null | ✓ | ✓ |  |
| N625-004 | Indefinite ordered set of classwide tagged values | ✓ | ✓ | Both hosts check empty state/cursors, insertion and duplicate detection, lookup/element retrieval, cursor advancement, independent copying, deletion and clearing. Less returns False so null-record values form one equivalence class; the original always-True comparator violated the ordered-set contract. |
| N707-022 | Constrained array of aliased fixed-length strings | XFAIL | XFAIL | Original Ada and first-element read retained. Scanner correctly skips the constrained array type and dependent global; the host API required by the mains is unavailable. Scanner baseline checks the diagnostics. |
---

## O-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| O114-019 | `Rec` record + `Rec_Ptr` access + global `Global : Rec_Ptr` + `Say(String)` procedure | ✓ | ✓ |  |

---

## PA / PB / PC-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| PA18-031 | Exceptions + enum array access (custom test) | Question | Question | AJIS gnatdoc/javadoc tooling test; only the Say part is portable |
| PA18-032 | Enum array access (custom test) | Question | Question | AJIS javadoc-without-warnings tooling test; run part passes |
| PA18-039 | Record + procedure | Question | Question | AJIS javadoc text check only (no test.out); port prints OK |
| PA18-043 | Enum + inheritance + procedure | Question | Question | the test uses the AJIS API for range types and subtypes (`X.FIRST`, `X.First ()`, `X.elements ()`), with no equivalent: range types are bound as their base type (see doc/limitations.rst); also uses `Long_Long_Float` (128 bits) and arrays with a 64-bit index |
| PA18-045 | Simple procedure | Question | Question | passes, but T/w (System.Dim.Mks, Long_Long_Float) are not bound while AJIS bound them |
| PB09-020 | Multiple packages + controlled + abstract + AJIS | XFAIL | XFAIL | XFAIL: bug: the shadow wrapper copies the limited object returned by a dispatching primitive, so the proxy does not compile; AJIS `Bind` annotations (which only hid the functions returning `Group_Block`) removed, and `bindings.ads` (annotated renamings, not bound by `test.cmd`) not imported |
| PB30-010 | Incomplete declarations, abstract/classwide iterators and aliased record-array results | XFAIL | XFAIL | All three original units and the original call sequence retained, including anonymous-access Should_Be_Bound calls. Scanner crashes on incomplete Parameter_Array; interfaces and anonymous access are additional limitations. |
| PC16-003 | Tagged record with all parameter modes (by-value, class-wide, access, in-out, out) | ✓ | ✓ | C++ exercises all twelve parameter modes and checks writebacks, including by-reference aliasing. Java covers the supported calls. Host subclass cases remain unavailable because `AdaRecord` is final (several controlling parameters). AJIS reflection optimization checks do not apply. |
| PC16-004 | Plain record + tagged record with `Msg` (String return) + class-wide `Say`; AJIS `Rename` pragmas stripped | ✓ | ✓ | Both ports exercise plain/tagged records and host overrides of `Msg`; Java returns `PolyglotString`. AJIS-specific class-renaming checks are excluded; `Binding_Renaming_Type` subtype is not separately exported. |
| PC16-007-1 | Generic list instantiations + Iterable record types + tagged class-wide iterators | Question | Question | the test checks the AJIS mapping of `Iterable` types and container instantiations to `java.lang.Iterable` (AJIS iterator options): same open question as QA16-045; the instantiations themselves are bound |
| PC16-007-2 | Generic list instantiations + Iterable record types + tagged class-wide iterators (variant 2 — identical Ada sources to PC16-007-1) | Question | Question | the test checks the AJIS mapping of `Iterable` types and container instantiations to `java.lang.Iterable` (AJIS iterator options): same open question as QA16-045; the instantiations themselves are bound |
| PC16-007-3 | Generic list instantiations + Iterable record types + tagged class-wide iterators (variant 3 — identical Ada sources to PC16-007-1) | Question | Question | the test checks the AJIS mapping of `Iterable` types and container instantiations to `java.lang.Iterable` (AJIS iterator options): same open question as QA16-045; the instantiations themselves are bound |
| PC16-007-4 | Generic list instantiations + Iterable record types + tagged class-wide iterators (variant 4 — identical Ada sources to PC16-007-1) | Question | Question | the test checks the AJIS mapping of `Iterable` types and container instantiations to `java.lang.Iterable` (AJIS iterator options): same open question as QA16-045; the instantiations themselves are bound |

---

## Q / QA / QB / QC-series

| Test | Description | cpp | java | Notes |
|------|-------------|-----|------|-------|
| Q221-033 | Record with `Triboolean` (enum from GNATCOLL child package) + `Say (String)` | ✓ | ✓ | `Triboolean` enum from `GNATCOLL.Tribooleans` is fully bound in C++ |
| Q605-017 | Tagged inheritance `Parent_Type` → `Child_Type` + `Proc` (access to classwide params, not bound) + `Say` methods | ✓ | ✓ |  |
| Q704-014 | Formal doubly linked list instantiation alongside Say | XFAIL | XFAIL | Original Ada and Say main retained. Source does not compile with this toolchain: Ada.Containers.Formal_Doubly_Linked_Lists moved to the SPARK library. No dependency or Ada-source substitution made. |
| Q712-033 | Access tagged function | XFAIL | XFAIL | XFAIL: bug: subtypes of a class-wide type break the proxy (`ST1_Classwide` not declared), so no binding is generated: the ports are written against the expected API; limitation: anonymous access types are not yet supported (`FA1`, `FA2`, `P1`, `P2`) |
| Q727-003 | Incomplete types (forward declarations) | XFAIL | XFAIL | XFAIL: limitation: anonymous access types are not yet supported (the access discriminants of `Reference_Type` and `Reference_Type_Not_Tagged`); AJIS `setOwner (NATIVE)` ported as `_setOwner (LIBRARY)` in Java, and as static `polyglot_ptr`s in C++ |
| Q804-001 | Dynamically generated packages (shell script) | No test | No test | Package bodies generated at test-run time; not portable |
| Q807-003 | Multiple packages + private record + AJIS annotations | XFAIL | XFAIL | XFAIL: missing feature: the predefined `"="` of `A_Type` is not mapped (Java `equals` is identity, no C++ `operator==`); AJIS `Attached` annotations removed, so `Null_Value` is a package constant |
| Q817-002 | Interface hierarchy used only from Ada (`Hello_World`) | ✓ | ✓ | the host program does not use them |
| Q913-009 | Multiple packages + exception procedure | Question | Question | exception renamings are not bound; catching the renamed exception passes |
| Q913-011 | Simple procedure (package `Hello`) | ✓ | ✓ | AJIS test.out also had AJIS-specific exception output |
| QA13-016 | Generic dispatching iterators with limited types and access discriminants | XFAIL | XFAIL | Next returns the iterator's stored self-referencing cursor, fixing the original Ada accessibility failure; native Ada iteration over 0 through 10 is verified. Proxy generation still copies a limited classwide iterator result, causing compilation failure. Both mains retain the host-language iteration check, whose Iterable mapping is also unsupported. |
| QA16-045 | Tagged type with cursor-style functions | Question | Question | Iterable aspect not mapped; explicit loop passes |
| QB20-023 | Character-literal enum procedure (Java enum) | XFAIL | XFAIL | XFAIL: limitation: enumerations with character literals are not supported (`T_VCS_CODE_CHARACTER`); bug: subprograms using an unbound type are kept, giving an invalid proxy (`Say`) |
| QB20-023-no-java-enum | Character-literal enum procedure (Ada-only enum) | Duplicate | Duplicate | same test as QB20-023 with an AJIS-only option |
| QC20-012 | Integer range types + procedure | Question | Question | the test uses the AJIS API for range types and subtypes (`X.FIRST`, `X.First ()`, `X.elements ()`), with no equivalent: range types are bound as their base type (see doc/limitations.rst) |
---

## Tests with no Java file (nothing to import)

The following AJIS tests have no `.java` file and are therefore out of scope:

`0000-000` · `H523-009` · `H715-012` · `H909-006` · `HC01-009` · `I105-010` ·
`L718-009` · `N707-033` · `OC14-022` · `Z999-999`

The following AJIS tests have no `test.java`, but one `test.java.<variant>`
file per AJIS option (naming, attached, function aliases...); they have not
been looked at yet:

`PA18-033` · `PA18-038` · `PA18-041` · `PC16-005` · `PC16-006` · `Q530-004`
