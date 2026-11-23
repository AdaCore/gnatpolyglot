package body Test is

   function Get_I (V : A_Tagged_Record) return Integer is
   begin
      return V.I;
   end Get_I;

end Test;
