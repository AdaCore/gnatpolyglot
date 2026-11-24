with Ada.Finalization; use Ada.Finalization;

package P is

   type Structured_File_Access is null record;

   type Update_Lock is limited new Limited_Controlled with private;

   function Lock_Updates (File : Structured_File_Access) return Update_Lock;

   procedure Unlock (This : in out Update_Lock);

   overriding procedure Finalize (This : in out Update_Lock);

private

   type Update_Lock is limited new Limited_Controlled with null record;

end P;
