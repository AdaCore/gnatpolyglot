package Pkg is

   type String_Access is access all String;

   type Int_Array is array (Integer range <>) of Integer;
   type Int_Array_Access is access all Int_Array;

   type Rec is record
      C : String_Access;
   end record;

   V : String_Access;
   A : Int_Array_Access;

   procedure Clobber;
   --  Overwrite the stack area left by the previous proxy call

   function V_Bounds return String;
   function A_Bounds return String;
   function C_Bounds (X : Rec) return String;

   function V_Is_Null return Boolean is (V = null);
   --  Image of the bounds currently stored in V, A and X.C (read directly in
   --  Ada, without going back through the proxy conversion).

end Pkg;
