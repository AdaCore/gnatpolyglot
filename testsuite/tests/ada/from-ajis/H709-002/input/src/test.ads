package Test is

   type A_Record is tagged record
      A_Value : Integer := 999;
   end record;

   function Get_Obj return A_Record'Class;

   procedure Proc (This : in out A_Record);

   procedure Call_Proc (This : in out A_Record'Class);

end Test;
