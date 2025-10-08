package Test is

   type T1 is tagged null record;

   procedure Overloaded_Proc (Self : T1);
   procedure Overloaded_Proc (Self : T1; A : Integer);

   procedure Call_Overload (Self : T1'Class);
   procedure Call_Overload (Self : T1'Class; A : Integer);

end Test;
