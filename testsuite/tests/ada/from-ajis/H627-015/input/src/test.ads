package Test is

   type A_Type is tagged null record;

   type A_Record is record
      A_Value : Integer := 999;
   end record;

   type A_Record_Tagged is tagged record
      A_Value : Integer := 999;
   end record;

   function Get_A_Record_Tagged return A_Record_Tagged'Class;

   procedure A_Proc (T : A_Type; R : in out A_Record; R2 : in out A_Record_Tagged'Class);

   procedure Call_A_Proc (T : A_Type'Class; R : in out A_Record; R2 : in out A_Record_Tagged'Class);

end Test;
