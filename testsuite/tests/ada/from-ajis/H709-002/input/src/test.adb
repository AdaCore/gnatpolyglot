package body Test is

   function Get_Obj return A_Record'Class is
      R : A_Record;
   begin
      return R;
   end Get_Obj;

   procedure Proc (This : in out A_Record) is
   begin
      This.A_Value := 1024;
   end Proc;

   procedure Call_Proc (This : in out A_Record'Class) is
   begin
      Proc (This);
   end Call_Proc;

end Test;