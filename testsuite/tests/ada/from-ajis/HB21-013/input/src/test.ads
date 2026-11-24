package Test is

   type A_Tagged_Type is tagged record
      V : Integer := 0;
   end record;

   function Prim (V : access A_Tagged_Type) return access A_Tagged_Type;

   function P2 (V : access A_Tagged_Type) return Integer;

   Sth : access A_Tagged_Type := new A_Tagged_Type;

   function Call_Prim
     (V : access A_Tagged_Type'Class) return access A_Tagged_Type'Class;

end Test;
