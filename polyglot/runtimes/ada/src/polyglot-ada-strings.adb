--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;

package body Polyglot.Ada.Strings is

   -------------------
   -- To_C_Char_Ptr --
   -------------------

   function To_C_Char_Ptr
     (Str : Polyglot_String) return Interfaces.C.Strings.chars_ptr
   is
      Len : Interfaces.C.int := Length (Str);
      Str_Value : String (1 .. Natural (Len))
      with Address => Str.Data;
   begin
      return Interfaces.C.Strings.New_String (Str_Value);
   end To_C_Char_Ptr;

   ---------------------
   -- From_C_Char_Ptr --
   ---------------------

   function From_C_Char_Ptr
     (Ptr : Interfaces.C.Strings.chars_ptr) return Polyglot_String
   is
      Tmp_Renames : Interfaces.C.Strings.chars_ptr renames Ptr;
      Tmp_Access  : String_Access
      with Address => Tmp_Renames'Address;
      pragma Import (Ada, Tmp_Access);
      Len : Natural := Natural (Interfaces.C.Strings.Strlen (Ptr));
      Str         : String (1 .. Len)
      with Address => Tmp_Access.all'Address;
      New_Str     : String_Access := new String'(Str);
   begin
      return
        (First => 1,
         Last => Interfaces.C.Int (New_Str.all'Last),
         Data   => New_Str.all'Address);
   end From_C_Char_Ptr;

   ---------------------
   -- Free_C_Char_Ptr --
   ---------------------

   procedure Free_C_Char_Ptr (Item : Interfaces.C.Strings.chars_ptr) is
      Ptr : Interfaces.C.Strings.chars_ptr := Item;
   begin
      Interfaces.C.Strings.Free (Ptr);
   end Free_C_Char_Ptr;

end Polyglot.Ada.Strings;
