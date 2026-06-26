package Test is

   type T is null record;
   -- T Type Doc

   function Primitive(A : T) return T is (null record);

   procedure P is null;
   -- paragraph1
   -- line1
   -- line2
   --
   -- paragraph2
   -- line1
   -- line2

   procedure Incorrect_Format is null;
   --@foo doc

   type T_Acc is access all T;

   function F (A: T_Acc) return T_Acc is (null);

end Test;
