with Ada.Text_IO; use Ada.Text_IO;

package body P is

   function Lock_Updates (File : Structured_File_Access) return Update_Lock is
   begin
      return (Limited_Controlled with others => <>);
   end Lock_Updates;

   procedure Unlock (This : in out Update_Lock) is
   begin
      Put_Line ("UNLOCK CALLED");
   end Unlock;

   overriding procedure Finalize (This : in out Update_Lock) is
   begin
      null;
   end Finalize;

end P;