with Polyglot.Ada.Arrays; use Polyglot.Ada.Arrays;

package Polyglot.Ada.Arrays.Native is

   ------------------------
   -- Short_Short arrays --
   ------------------------

   function Short_Short_Alloc is new Alloc (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Alloc, "polyglot__ada__arrays__native__short_short_array_alloc");

   procedure Short_Short_Construct is new Construct (Interfaces.C.char);
   pragma
     Export
       (C,
        Short_Short_Construct,
        "polyglot__ada__arrays__native__short_short_array_construct");

   function Short_Short_Clone is new Clone (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Clone, "polyglot__ada__arrays__native__short_short_array_clone");

   procedure Short_Short_Copy is new Copy (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Copy, "polyglot__ada__arrays__native__short_short_array_copy");

   procedure Short_Short_Free is new Free (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Free, "polyglot__ada__arrays__native__short_short_array_free");

   function Short_Short_Get is new Get (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Get, "polyglot__ada__arrays__native__short_short_array_get");

   procedure Short_Short_Set is new Set (Interfaces.C.char);
   pragma
     Export (C, Short_Short_Set, "polyglot__ada__arrays__native__short_short_array_set");

   ---------------------------------
   -- Unsigned_Short_Short arrays --
   ---------------------------------

   function Unsigned_Short_Short_Alloc is new Alloc (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Alloc, "polyglot__ada__arrays__native__unsigned_short_short_array_alloc");

   procedure Unsigned_Short_Short_Construct is new Construct (Interfaces.C.unsigned_char);
   pragma
     Export
       (C,
        Unsigned_Short_Short_Construct,
        "polyglot__ada__arrays__native__unsigned_short_short_array_construct");

   function Unsigned_Short_Short_Clone is new Clone (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Clone, "polyglot__ada__arrays__native__unsigned_short_short_array_clone");

   procedure Unsigned_Short_Short_Copy is new Copy (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Copy, "polyglot__ada__arrays__native__unsigned_short_short_array_copy");

   procedure Unsigned_Short_Short_Free is new Free (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Free, "polyglot__ada__arrays__native__unsigned_short_short_array_free");

   function Unsigned_Short_Short_Get is new Get (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Get, "polyglot__ada__arrays__native__unsigned_short_short_array_get");

   procedure Unsigned_Short_Short_Set is new Set (Interfaces.C.unsigned_char);
   pragma
     Export (C, Unsigned_Short_Short_Set, "polyglot__ada__arrays__native__unsigned_short_short_array_set");

   ------------------
   -- Short arrays --
   ------------------

   function Short_Alloc is new Alloc (Interfaces.C.short);
   pragma
     Export (C, Short_Alloc, "polyglot__ada__arrays__native__short_array_alloc");

   procedure Short_Construct is new Construct (Interfaces.C.short);
   pragma
     Export
       (C,
        Short_Construct,
        "polyglot__ada__arrays__native__short_array_construct");

   function Short_Clone is new Clone (Interfaces.C.short);
   pragma
     Export (C, Short_Clone, "polyglot__ada__arrays__native__short_array_clone");

   procedure Short_Copy is new Copy (Interfaces.C.short);
   pragma
     Export (C, Short_Copy, "polyglot__ada__arrays__native__short_array_copy");

   procedure Short_Free is new Free (Interfaces.C.short);
   pragma
     Export (C, Short_Free, "polyglot__ada__arrays__native__short_array_free");

   function Short_Get is new Get (Interfaces.C.short);
   pragma
     Export (C, Short_Get, "polyglot__ada__arrays__native__short_array_get");

   procedure Short_Set is new Set (Interfaces.C.short);
   pragma
     Export (C, Short_Set, "polyglot__ada__arrays__native__short_array_set");

   ---------------------------
   -- Unsigned_Short arrays --
   ---------------------------

   function Unsigned_Short_Alloc is new Alloc (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Alloc, "polyglot__ada__arrays__native__unsigned_short_array_alloc");

   procedure Unsigned_Short_Construct is new Construct (Interfaces.C.unsigned_short);
   pragma
     Export
       (C,
        Unsigned_Short_Construct,
        "polyglot__ada__arrays__native__unsigned_short_array_construct");

   function Unsigned_Short_Clone is new Clone (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Clone, "polyglot__ada__arrays__native__unsigned_short_array_clone");

   procedure Unsigned_Short_Copy is new Copy (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Copy, "polyglot__ada__arrays__native__unsigned_short_array_copy");

   procedure Unsigned_Short_Free is new Free (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Free, "polyglot__ada__arrays__native__unsigned_short_array_free");

   function Unsigned_Short_Get is new Get (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Get, "polyglot__ada__arrays__native__unsigned_short_array_get");

   procedure Unsigned_Short_Set is new Set (Interfaces.C.unsigned_short);
   pragma
     Export (C, Unsigned_Short_Set, "polyglot__ada__arrays__native__unsigned_short_array_set");

   ----------------
   -- Int arrays --
   ----------------

   function Int_Alloc is new Alloc (Interfaces.C.int);
   pragma
     Export (C, Int_Alloc, "polyglot__ada__arrays__native__int_array_alloc");

   procedure Int_Construct is new Construct (Interfaces.C.int);
   pragma
     Export
       (C,
        Int_Construct,
        "polyglot__ada__arrays__native__int_array_construct");

   function Int_Clone is new Clone (Interfaces.C.int);
   pragma
     Export (C, Int_Clone, "polyglot__ada__arrays__native__int_array_clone");

   procedure Int_Copy is new Copy (Interfaces.C.int);
   pragma
     Export (C, Int_Copy, "polyglot__ada__arrays__native__int_array_copy");

   procedure Int_Free is new Free (Interfaces.C.int);
   pragma
     Export (C, Int_Free, "polyglot__ada__arrays__native__int_array_free");

   function Int_Get is new Get (Interfaces.C.int);
   pragma
     Export (C, Int_Get, "polyglot__ada__arrays__native__int_array_get");

   procedure Int_Set is new Set (Interfaces.C.int);
   pragma
     Export (C, Int_Set, "polyglot__ada__arrays__native__int_array_set");

   ---------------------------
   -- Unsigned_Int arrays --
   ---------------------------

   function Unsigned_Int_Alloc is new Alloc (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Alloc, "polyglot__ada__arrays__native__unsigned_int_array_alloc");

   procedure Unsigned_Int_Construct is new Construct (Interfaces.C.unsigned);
   pragma
     Export
       (C,
        Unsigned_Int_Construct,
        "polyglot__ada__arrays__native__unsigned_int_array_construct");

   function Unsigned_Int_Clone is new Clone (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Clone, "polyglot__ada__arrays__native__unsigned_int_array_clone");

   procedure Unsigned_Int_Copy is new Copy (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Copy, "polyglot__ada__arrays__native__unsigned_int_array_copy");

   procedure Unsigned_Int_Free is new Free (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Free, "polyglot__ada__arrays__native__unsigned_int_array_free");

   function Unsigned_Int_Get is new Get (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Get, "polyglot__ada__arrays__native__unsigned_int_array_get");

   procedure Unsigned_Int_Set is new Set (Interfaces.C.unsigned);
   pragma
     Export (C, Unsigned_Int_Set, "polyglot__ada__arrays__native__unsigned_int_array_set");

   ------------------
   -- Long arrays --
   ------------------

   function Long_Alloc is new Alloc (Interfaces.C.long);
   pragma
     Export (C, Long_Alloc, "polyglot__ada__arrays__native__long_array_alloc");

   procedure Long_Construct is new Construct (Interfaces.C.long);
   pragma
     Export
       (C,
        Long_Construct,
        "polyglot__ada__arrays__native__long_array_construct");

   function Long_Clone is new Clone (Interfaces.C.long);
   pragma
     Export (C, Long_Clone, "polyglot__ada__arrays__native__long_array_clone");

   procedure Long_Copy is new Copy (Interfaces.C.long);
   pragma
     Export (C, Long_Copy, "polyglot__ada__arrays__native__long_array_copy");

   procedure Long_Free is new Free (Interfaces.C.long);
   pragma
     Export (C, Long_Free, "polyglot__ada__arrays__native__long_array_free");

   function Long_Get is new Get (Interfaces.C.long);
   pragma
     Export (C, Long_Get, "polyglot__ada__arrays__native__long_array_get");

   procedure Long_Set is new Set (Interfaces.C.long);
   pragma
     Export (C, Long_Set, "polyglot__ada__arrays__native__long_array_set");

   ------------------
   -- Unsigned_Long arrays --
   ------------------

   function Unsigned_Long_Alloc is new Alloc (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Alloc, "polyglot__ada__arrays__native__unsigned_long_array_alloc");

   procedure Unsigned_Long_Construct is new Construct (Interfaces.C.unsigned_long);
   pragma
     Export
       (C,
        Unsigned_Long_Construct,
        "polyglot__ada__arrays__native__unsigned_long_array_construct");

   function Unsigned_Long_Clone is new Clone (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Clone, "polyglot__ada__arrays__native__unsigned_long_array_clone");

   procedure Unsigned_Long_Copy is new Copy (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Copy, "polyglot__ada__arrays__native__unsigned_long_array_copy");

   procedure Unsigned_Long_Free is new Free (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Free, "polyglot__ada__arrays__native__unsigned_long_array_free");

   function Unsigned_Long_Get is new Get (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Get, "polyglot__ada__arrays__native__unsigned_long_array_get");

   procedure Unsigned_Long_Set is new Set (Interfaces.C.unsigned_long);
   pragma
     Export (C, Unsigned_Long_Set, "polyglot__ada__arrays__native__unsigned_long_array_set");

end Polyglot.Ada.Arrays.Native;
