with Interfaces.C; use Interfaces.C;
with Interfaces.C.Strings;
with System;

package Polyglot.Ada.Arrays is

   type Fat_Pointer is record
      Data       : System.Address;
      Descriptor : System.Address;
   end record
   with Convention => C;

   type Polyglot_Array is record
      First, Last : Interfaces.C.Int;
      Data        : System.Address;
   end record
   with Convention => C_Pass_By_Copy;

   generic
      type C is private;
   function Alloc
     (First : Interfaces.C.Int; Last : Interfaces.C.Int) return Polyglot_Array;

   generic
      type C is private;
   procedure Construct
     (Self  : System.Address;
      First : Interfaces.C.Int;
      Last  : Interfaces.C.Int);

   generic
      type C is private;
   function Clone (Self : Polyglot_Array) return Polyglot_Array;

   generic
      type C is private;
   procedure Copy (To : System.Address; From : Polyglot_Array);

   generic
      type C is private;
   procedure Free (Self : System.Address);

   generic
      type C is private;
   function Get
     (Self : Polyglot_Array; Index : Interfaces.C.Int) return System.Address;

   generic
      type C is private;
   procedure Set
     (Self : Polyglot_Array; Index : Interfaces.C.Int; New_Val : C);

   function Length (Arr : Polyglot_Array) return Interfaces.C.int
   is (Arr.Last - Arr.First + 1);

end Polyglot.Ada.Arrays;
