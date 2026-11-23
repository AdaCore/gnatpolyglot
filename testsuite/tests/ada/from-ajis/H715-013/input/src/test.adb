with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Proc
     (Callback : access function (S : String) return Boolean := null)
   is
   begin
      if Callback /= null then
         Put_Line (Boolean'Image (Callback ("TRUE")));
         Put_Line (Boolean'Image (Callback ("FALSE")));
      else
         Put_Line ("NULL CALLBACK");
      end if;
   end Proc;

end Test;