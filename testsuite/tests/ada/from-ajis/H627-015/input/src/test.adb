with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Get_A_Record_Tagged return A_Record_Tagged'Class is
      Result : A_Record_Tagged;
   begin
      return Result;
   end Get_A_Record_Tagged;

   procedure A_Proc (T : A_Type; R : in out A_Record; R2 : in out A_Record_Tagged'Class) is
   begin
      R.A_Value := 1024;
      R2.A_Value := 1024;
   end A_Proc;

   procedure Call_A_Proc (T : A_Type'Class; R : in out A_Record; R2 : in out A_Record_Tagged'Class) is
   begin
      A_Proc (T, R, R2);
   end Call_A_Proc;

end Test;