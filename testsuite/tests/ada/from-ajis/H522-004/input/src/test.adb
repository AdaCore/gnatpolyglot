with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Same_Rec
     (V : Boolean; Obj : A_Obj) return A_Obj is
   begin
      return Obj;
   end Get_Same_Rec;

end Test;