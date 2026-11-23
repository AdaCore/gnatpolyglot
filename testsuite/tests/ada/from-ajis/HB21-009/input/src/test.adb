with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Call_Char
     (X : Ret_Char; V : Character) return Character is
   begin
      return X.all (V);
   end Call_Char;

   function Call_Long
     (X : Ret_Long; V : Long_Long_Integer) return Long_Long_Integer is
   begin
      return X.all (V);
   end Call_Long;

end Test;
