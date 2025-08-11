with Interfaces.C; use Interfaces.C;
with Interfaces.C.Strings;

with Polyglot.Ada.Arrays; use Polyglot.Ada.Arrays;

with System;

package Polyglot.Ada.Strings is

   type Polyglot_String is record
      First, Last : Interfaces.C.Int;
      Data        : System.Address;
   end record
   with Convention => C_Pass_By_Copy;

   type String_Access is access all String with Size => Standard'Address_Size;

   function Length (Arr : Polyglot_String) return Interfaces.C.int
   is (Arr.Last - Arr.First + 1);

   function String_Alloc is new Alloc (Interfaces.C.char);
   pragma Export (C, String_Alloc, "polyglot__ada__strings__string_alloc");

   procedure String_Construct is new Construct (Interfaces.C.char);
   pragma
     Export (C, String_Construct, "polyglot__ada__strings__string_construct");

   function String_Clone is new Clone (Interfaces.C.char);
   pragma Export (C, String_Clone, "polyglot__ada__strings__string_clone");

   procedure String_Copy is new Copy (Interfaces.C.char);
   pragma Export (C, String_Copy, "polyglot__ada__strings__string_copy");

   procedure String_Free is new Free (Interfaces.C.char);
   pragma Export (C, String_Free, "polyglot__ada__strings__string_free");

   function String_Get is new Get (Interfaces.C.char);
   pragma Export (C, String_Get, "polyglot__ada__strings__string_get");

   procedure String_Set is new Set (Interfaces.C.char);
   pragma Export (C, String_Set, "polyglot__ada__strings__string_set");

   --------------------------
   -- Conversion functions --
   --------------------------

   function To_C_Char_Ptr
     (Str : Polyglot_String) return Interfaces.C.Strings.chars_ptr;
   pragma Export (C, To_C_Char_Ptr, "polyglot__ada__strings_to_c_chars_ptr");

   function From_C_Char_Ptr
     (Ptr : Interfaces.C.Strings.chars_ptr) return Polyglot_String;
   pragma
     Export (C, From_C_Char_Ptr, "polyglot__ada__strings_from_c_chars_ptr");

   procedure Free_C_Char_Ptr (Item : Interfaces.C.Strings.chars_ptr);
   pragma
     Export (C, Free_C_Char_Ptr, "polyglot__ada__strings_free_c_chars_ptr");

end Polyglot.Ada.Strings;
