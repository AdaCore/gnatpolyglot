--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with GNATpolyglot.Ada.Arrays; use GNATpolyglot.Ada.Arrays;
with Interfaces;

package GNATpolyglot.Ada.Arrays.Native is

   ------------------------
   -- Short_Short arrays --
   ------------------------

   function Short_Short_Alloc is new Alloc (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Alloc, "gnatpolyglot__ada__arrays__native__short_short_array_alloc");

   procedure Short_Short_Construct is new Construct (Interfaces.Integer_8);
   pragma
     Export
       (C,
        Short_Short_Construct,
        "gnatpolyglot__ada__arrays__native__short_short_array_construct");

   function Short_Short_Clone is new Clone (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Clone, "gnatpolyglot__ada__arrays__native__short_short_array_clone");

   procedure Short_Short_Copy is new Copy (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Copy, "gnatpolyglot__ada__arrays__native__short_short_array_copy");

   procedure Short_Short_Free is new Free (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Free, "gnatpolyglot__ada__arrays__native__short_short_array_free");

   function Short_Short_Get is new Get (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Get, "gnatpolyglot__ada__arrays__native__short_short_array_get");

   procedure Short_Short_Set is new Set (Interfaces.Integer_8);
   pragma
     Export (C, Short_Short_Set, "gnatpolyglot__ada__arrays__native__short_short_array_set");

   ---------------------------------
   -- Unsigned_Short_Short arrays --
   ---------------------------------

   function Unsigned_Short_Short_Alloc is new Alloc (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Alloc, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_alloc");

   procedure Unsigned_Short_Short_Construct is new Construct (Interfaces.Unsigned_8);
   pragma
     Export
       (C,
        Unsigned_Short_Short_Construct,
        "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_construct");

   function Unsigned_Short_Short_Clone is new Clone (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Clone, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_clone");

   procedure Unsigned_Short_Short_Copy is new Copy (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Copy, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_copy");

   procedure Unsigned_Short_Short_Free is new Free (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Free, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_free");

   function Unsigned_Short_Short_Get is new Get (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Get, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_get");

   procedure Unsigned_Short_Short_Set is new Set (Interfaces.Unsigned_8);
   pragma
     Export (C, Unsigned_Short_Short_Set, "gnatpolyglot__ada__arrays__native__unsigned_short_short_array_set");

   ------------------
   -- Short arrays --
   ------------------

   function Short_Alloc is new Alloc (Interfaces.Integer_16);
   pragma
     Export (C, Short_Alloc, "gnatpolyglot__ada__arrays__native__short_array_alloc");

   procedure Short_Construct is new Construct (Interfaces.Integer_16);
   pragma
     Export
       (C,
        Short_Construct,
        "gnatpolyglot__ada__arrays__native__short_array_construct");

   function Short_Clone is new Clone (Interfaces.Integer_16);
   pragma
     Export (C, Short_Clone, "gnatpolyglot__ada__arrays__native__short_array_clone");

   procedure Short_Copy is new Copy (Interfaces.Integer_16);
   pragma
     Export (C, Short_Copy, "gnatpolyglot__ada__arrays__native__short_array_copy");

   procedure Short_Free is new Free (Interfaces.Integer_16);
   pragma
     Export (C, Short_Free, "gnatpolyglot__ada__arrays__native__short_array_free");

   function Short_Get is new Get (Interfaces.Integer_16);
   pragma
     Export (C, Short_Get, "gnatpolyglot__ada__arrays__native__short_array_get");

   procedure Short_Set is new Set (Interfaces.Integer_16);
   pragma
     Export (C, Short_Set, "gnatpolyglot__ada__arrays__native__short_array_set");

   ---------------------------
   -- Unsigned_Short arrays --
   ---------------------------

   function Unsigned_Short_Alloc is new Alloc (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Alloc, "gnatpolyglot__ada__arrays__native__unsigned_short_array_alloc");

   procedure Unsigned_Short_Construct is new Construct (Interfaces.Unsigned_16);
   pragma
     Export
       (C,
        Unsigned_Short_Construct,
        "gnatpolyglot__ada__arrays__native__unsigned_short_array_construct");

   function Unsigned_Short_Clone is new Clone (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Clone, "gnatpolyglot__ada__arrays__native__unsigned_short_array_clone");

   procedure Unsigned_Short_Copy is new Copy (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Copy, "gnatpolyglot__ada__arrays__native__unsigned_short_array_copy");

   procedure Unsigned_Short_Free is new Free (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Free, "gnatpolyglot__ada__arrays__native__unsigned_short_array_free");

   function Unsigned_Short_Get is new Get (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Get, "gnatpolyglot__ada__arrays__native__unsigned_short_array_get");

   procedure Unsigned_Short_Set is new Set (Interfaces.Unsigned_16);
   pragma
     Export (C, Unsigned_Short_Set, "gnatpolyglot__ada__arrays__native__unsigned_short_array_set");

   ----------------
   -- Int arrays --
   ----------------

   function Int_Alloc is new Alloc (Interfaces.Integer_32);
   pragma
     Export (C, Int_Alloc, "gnatpolyglot__ada__arrays__native__int_array_alloc");

   procedure Int_Construct is new Construct (Interfaces.Integer_32);
   pragma
     Export
       (C,
        Int_Construct,
        "gnatpolyglot__ada__arrays__native__int_array_construct");

   function Int_Clone is new Clone (Interfaces.Integer_32);
   pragma
     Export (C, Int_Clone, "gnatpolyglot__ada__arrays__native__int_array_clone");

   procedure Int_Copy is new Copy (Interfaces.Integer_32);
   pragma
     Export (C, Int_Copy, "gnatpolyglot__ada__arrays__native__int_array_copy");

   procedure Int_Free is new Free (Interfaces.Integer_32);
   pragma
     Export (C, Int_Free, "gnatpolyglot__ada__arrays__native__int_array_free");

   function Int_Get is new Get (Interfaces.Integer_32);
   pragma
     Export (C, Int_Get, "gnatpolyglot__ada__arrays__native__int_array_get");

   procedure Int_Set is new Set (Interfaces.Integer_32);
   pragma
     Export (C, Int_Set, "gnatpolyglot__ada__arrays__native__int_array_set");

   ---------------------------
   -- Unsigned_Int arrays --
   ---------------------------

   function Unsigned_Int_Alloc is new Alloc (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Alloc, "gnatpolyglot__ada__arrays__native__unsigned_int_array_alloc");

   procedure Unsigned_Int_Construct is new Construct (Interfaces.Unsigned_32);
   pragma
     Export
       (C,
        Unsigned_Int_Construct,
        "gnatpolyglot__ada__arrays__native__unsigned_int_array_construct");

   function Unsigned_Int_Clone is new Clone (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Clone, "gnatpolyglot__ada__arrays__native__unsigned_int_array_clone");

   procedure Unsigned_Int_Copy is new Copy (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Copy, "gnatpolyglot__ada__arrays__native__unsigned_int_array_copy");

   procedure Unsigned_Int_Free is new Free (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Free, "gnatpolyglot__ada__arrays__native__unsigned_int_array_free");

   function Unsigned_Int_Get is new Get (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Get, "gnatpolyglot__ada__arrays__native__unsigned_int_array_get");

   procedure Unsigned_Int_Set is new Set (Interfaces.Unsigned_32);
   pragma
     Export (C, Unsigned_Int_Set, "gnatpolyglot__ada__arrays__native__unsigned_int_array_set");

   ------------------
   -- Long arrays --
   ------------------

   function Long_Alloc is new Alloc (Interfaces.Integer_64);
   pragma
     Export (C, Long_Alloc, "gnatpolyglot__ada__arrays__native__long_array_alloc");

   procedure Long_Construct is new Construct (Interfaces.Integer_64);
   pragma
     Export
       (C,
        Long_Construct,
        "gnatpolyglot__ada__arrays__native__long_array_construct");

   function Long_Clone is new Clone (Interfaces.Integer_64);
   pragma
     Export (C, Long_Clone, "gnatpolyglot__ada__arrays__native__long_array_clone");

   procedure Long_Copy is new Copy (Interfaces.Integer_64);
   pragma
     Export (C, Long_Copy, "gnatpolyglot__ada__arrays__native__long_array_copy");

   procedure Long_Free is new Free (Interfaces.Integer_64);
   pragma
     Export (C, Long_Free, "gnatpolyglot__ada__arrays__native__long_array_free");

   function Long_Get is new Get (Interfaces.Integer_64);
   pragma
     Export (C, Long_Get, "gnatpolyglot__ada__arrays__native__long_array_get");

   procedure Long_Set is new Set (Interfaces.Integer_64);
   pragma
     Export (C, Long_Set, "gnatpolyglot__ada__arrays__native__long_array_set");

   ------------------
   -- Unsigned_Long arrays --
   ------------------

   function Unsigned_Long_Alloc is new Alloc (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Alloc, "gnatpolyglot__ada__arrays__native__unsigned_long_array_alloc");

   procedure Unsigned_Long_Construct is new Construct (Interfaces.Unsigned_64);
   pragma
     Export
       (C,
        Unsigned_Long_Construct,
        "gnatpolyglot__ada__arrays__native__unsigned_long_array_construct");

   function Unsigned_Long_Clone is new Clone (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Clone, "gnatpolyglot__ada__arrays__native__unsigned_long_array_clone");

   procedure Unsigned_Long_Copy is new Copy (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Copy, "gnatpolyglot__ada__arrays__native__unsigned_long_array_copy");

   procedure Unsigned_Long_Free is new Free (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Free, "gnatpolyglot__ada__arrays__native__unsigned_long_array_free");

   function Unsigned_Long_Get is new Get (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Get, "gnatpolyglot__ada__arrays__native__unsigned_long_array_get");

   procedure Unsigned_Long_Set is new Set (Interfaces.Unsigned_64);
   pragma
     Export (C, Unsigned_Long_Set, "gnatpolyglot__ada__arrays__native__unsigned_long_array_set");

   ------------------
   -- Float arrays --
   ------------------

   function Float_Alloc is new Alloc (Interfaces.C.C_float);
   pragma
     Export (C, Float_Alloc, "gnatpolyglot__ada__arrays__native__float_array_alloc");

   procedure Float_Construct is new Construct (Interfaces.C.C_float);
   pragma
     Export
       (C,
        Float_Construct,
        "gnatpolyglot__ada__arrays__native__float_array_construct");

   function Float_Clone is new Clone (Interfaces.C.C_float);
   pragma
     Export (C, Float_Clone, "gnatpolyglot__ada__arrays__native__float_array_clone");

   procedure Float_Copy is new Copy (Interfaces.C.C_float);
   pragma
     Export (C, Float_Copy, "gnatpolyglot__ada__arrays__native__float_array_copy");

   procedure Float_Free is new Free (Interfaces.C.C_float);
   pragma
     Export (C, Float_Free, "gnatpolyglot__ada__arrays__native__float_array_free");

   function Float_Get is new Get (Interfaces.C.C_float);
   pragma
     Export (C, Float_Get, "gnatpolyglot__ada__arrays__native__float_array_get");

   procedure Float_Set is new Set (Interfaces.C.C_float);
   pragma
     Export (C, Float_Set, "gnatpolyglot__ada__arrays__native__float_array_set");

   ------------------
   -- Double arrays --
   ------------------

   function Double_Alloc is new Alloc (Interfaces.C.double);
   pragma
     Export (C, Double_Alloc, "gnatpolyglot__ada__arrays__native__double_array_alloc");

   procedure Double_Construct is new Construct (Interfaces.C.double);
   pragma
     Export
       (C,
        Double_Construct,
        "gnatpolyglot__ada__arrays__native__double_array_construct");

   function Double_Clone is new Clone (Interfaces.C.double);
   pragma
     Export (C, Double_Clone, "gnatpolyglot__ada__arrays__native__double_array_clone");

   procedure Double_Copy is new Copy (Interfaces.C.double);
   pragma
     Export (C, Double_Copy, "gnatpolyglot__ada__arrays__native__double_array_copy");

   procedure Double_Free is new Free (Interfaces.C.double);
   pragma
     Export (C, Double_Free, "gnatpolyglot__ada__arrays__native__double_array_free");

   function Double_Get is new Get (Interfaces.C.double);
   pragma
     Export (C, Double_Get, "gnatpolyglot__ada__arrays__native__double_array_get");

   procedure Double_Set is new Set (Interfaces.C.double);
   pragma
     Export (C, Double_Set, "gnatpolyglot__ada__arrays__native__double_array_set");

   --------------------
   -- Boolean arrays --
   --------------------

   --  Standard.Boolean is a one-byte (0 => False, 1 => True) addressable
   --  component in an unpacked array, so it crosses the boundary as a byte the
   --  client reads as a Rust ``bool``.

   function Boolean_Alloc is new Alloc (Standard.Boolean);
   pragma
     Export (C, Boolean_Alloc, "gnatpolyglot__ada__arrays__native__boolean_array_alloc");

   procedure Boolean_Construct is new Construct (Standard.Boolean);
   pragma
     Export
       (C,
        Boolean_Construct,
        "gnatpolyglot__ada__arrays__native__boolean_array_construct");

   function Boolean_Clone is new Clone (Standard.Boolean);
   pragma
     Export (C, Boolean_Clone, "gnatpolyglot__ada__arrays__native__boolean_array_clone");

   procedure Boolean_Copy is new Copy (Standard.Boolean);
   pragma
     Export (C, Boolean_Copy, "gnatpolyglot__ada__arrays__native__boolean_array_copy");

   procedure Boolean_Free is new Free (Standard.Boolean);
   pragma
     Export (C, Boolean_Free, "gnatpolyglot__ada__arrays__native__boolean_array_free");

   function Boolean_Get is new Get (Standard.Boolean);
   pragma
     Export (C, Boolean_Get, "gnatpolyglot__ada__arrays__native__boolean_array_get");

   --  Boolean_Set passes the element as an 8-bit Ada Boolean; GNAT warns that
   --  the matching C type should be ``char`` (a byte). That is exactly the ABI:
   --  callers pass a 0/1 byte and the glue converts to/from the language's
   --  boolean type, so the warning is benign and suppressed here.
   pragma Warnings (Off, "*is an 8-bit Ada Boolean*");
   procedure Boolean_Set is new Set (Standard.Boolean);
   pragma Warnings (On, "*is an 8-bit Ada Boolean*");
   pragma
     Export (C, Boolean_Set, "gnatpolyglot__ada__arrays__native__boolean_array_set");

end GNATpolyglot.Ada.Arrays.Native;
