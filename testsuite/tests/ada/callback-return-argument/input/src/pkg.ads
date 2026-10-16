package Pkg is

   type Rec is record
      I : Integer;
   end record;

   --  A callback returning a record

   type Rec_Callback is access function (R : Rec) return Rec;

   procedure Call (C : Rec_Callback);
   --  Call C with a record whose component is 1, and print the component of
   --  the record it returns.

   --  A primitive returning a record, to be overridden

   type T is tagged null record;

   function Echo (Self : T; R : Rec) return Rec;
   --  Return R

   procedure Call_Echo (Self : T'Class);
   --  Call Echo with a record whose component is 2, and print the component
   --  of the record it returns.

end Pkg;
