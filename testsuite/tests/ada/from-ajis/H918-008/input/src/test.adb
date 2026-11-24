with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   Base_Val : Integer := 42;

   procedure Set_To_New (V : in out An_Array_Access) is
   begin
      Print_Contents (V);

      V := new An_Array'(1 => Base_Val, 2 => Base_Val + 1);
      Base_Val := Base_Val + 2;
   end Set_To_New;

   procedure Set_To_Null (V : in out An_Array_Access) is
   begin
      Print_Contents (V);

      V := null;
   end Set_To_Null;

   procedure Print_Contents (V : An_Array_Access) is
   begin
      if V = null then
         Put_Line ("null");
      else
         for J in V'Range loop
            Put (V (J)'Img);
         end loop;

         New_Line;
      end if;
   end Print_Contents;

   procedure Call_Set_To_Null_And_Print
     (R : Root'Class; V : in out An_Array_Access)
   is
   begin
      Print_Contents (V);
      Set_To_Null (R, V);
      Print_Contents (V);
   end Call_Set_To_Null_And_Print;

end Test;
