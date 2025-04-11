package Test is

   type T1 is record
      V: Integer;
   end record;

   procedure Overloaded_Proc;
   procedure Overloaded_Proc(A: Integer);

   function Overloaded_Fun(A, B: Integer; C: Integer) return Integer;
   function Overloaded_Fun(A: Integer; B, C: T1) return Integer;

end Test;
