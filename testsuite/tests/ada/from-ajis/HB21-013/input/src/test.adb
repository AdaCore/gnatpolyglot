package body Test is

   function Prim (V : access A_Tagged_Type) return access A_Tagged_Type is
   begin
      return V;
   end Prim;

   function P2 (V : access A_Tagged_Type) return Integer is
   begin
      return 0;
   end P2;

   function Call_Prim
     (V : access A_Tagged_Type'Class) return access A_Tagged_Type'Class is
   begin
      return V.Prim;
   end Call_Prim;

end Test;
