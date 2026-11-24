with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_Object (I : Integer) return Object is
   begin
      Put_Line ("GET OBJECT");
      return (null record);
   end Get_Object;

end Test;
