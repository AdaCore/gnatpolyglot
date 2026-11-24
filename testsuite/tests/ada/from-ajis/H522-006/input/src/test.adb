with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   type Internal_Child is new Root with record
      null;
   end record;

   procedure P_1 (V : Internal_Child) is
   begin
      Put_Line ("P_1 of Internal_Child");
   end P_1;

   procedure P_1 (V : Child_1) is
   begin
      Put_Line ("P_1 of Child_1");
   end P_1;

   function Get_Direct_Root_Child return Root'Class is
      V : Internal_Child;
   begin
      return V;
   end Get_Direct_Root_Child;

   procedure Display_P1 (Dummy : Boolean; V : Root'Class) is
   begin
      P_1 (V);
   end Display_P1;

end Test;
