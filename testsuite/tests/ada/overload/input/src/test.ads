package Test is

   type T1 is record
      V: Integer;
   end record;

   procedure Overloaded_Proc;
   procedure Overloaded_Proc(A: Integer);

   function Overloaded_Fun(A, B: Integer; C: Integer) return Integer;
   function Overloaded_Fun(A: Integer; B, C: T1) return Integer;

   procedure Overloaded_Ret;
   function Overloaded_Ret return Integer;
   function Overloaded_Ret return T1;
   function Overloaded_Ret(I: Integer) return Integer;

   function No_Rename (I: Integer) return Integer;
   function No_Rename (A, B: Long_Integer) return Long_Integer;

   type My_Int is new Integer;

   procedure Overloaded_Int (I: Integer);
   procedure Overloaded_Int (I: My_Int);

end Test;
