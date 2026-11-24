package Test is

   type T is tagged record
      F : Integer := 0;
   end record;

   type A_T is access all T'Class;

   procedure Set (B : Boolean; V : A_T);

   function Get_Reference return A_T;

   function Get_Copy return T'Class;

end Test;
