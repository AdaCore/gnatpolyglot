package Test is

   type Bla is tagged null record;

   type Some_Record (V : Integer) is null record;

   subtype Sub_Some_Record is Some_Record (10);

   procedure Prim
     (V      : Bla;
      Param1 : Natural;
      Param2 : Sub_Some_Record);

   procedure Call_Prim
     (V : Bla'Class;
      Param1 : Natural;
      Param2 : Sub_Some_Record);

end Test;
