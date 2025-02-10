with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_Param(A: Integer) is
   begin
      Put_Line("Hello from Ada P_Param:" & A'Image);
   end P_Param;

   function F_Param(A, B: Integer; C: Integer) return Integer is
   begin
      return A * B + C;
   end F_Param;

end Test;

