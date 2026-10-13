package Pkg is

   type Rec is record
      I : Integer;
   end record;

   type Rec_Access is access all Rec;

   type Rec_Callback is access function return Rec_Access;

   procedure Call (C : Rec_Callback);
   --  Print the component of the record returned by C, or "null"

   function Get return Rec_Callback;
   --  Return a callback returning a record whose component is 7

end Pkg;
