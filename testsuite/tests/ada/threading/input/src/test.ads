package Test is

   Exc : exception;

   procedure Get_Exception (I: Integer);

   procedure Unregister;

   type T is tagged null record;

   procedure Do_Copy (Obj: T'Class; I: Integer);

end Test;
