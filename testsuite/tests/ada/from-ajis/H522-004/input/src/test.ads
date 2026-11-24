with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Rec is tagged record
      null;
   end record;

   type A_Obj is access all Rec'Class;

   function Get_Same_Rec
     (V : Boolean; Obj : A_Obj) return A_Obj;

end Test;