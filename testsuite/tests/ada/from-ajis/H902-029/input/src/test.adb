with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Prim
     (V      : Bla;
      Param1 : Natural;
      Param2 : Sub_Some_Record) is
   begin
      Put_Line ("GOT FROM ADA :" & Param1'Img);
   end Prim;

   procedure Call_Prim
     (V : Bla'Class;
      Param1 : Natural;
      Param2 : Sub_Some_Record)
   is
   begin
      Prim (V, Param1, Param2);
   end Call_Prim;

end Test;
