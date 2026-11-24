package Test is

   type String_Access is access all String;

   V1 : Integer;
   V2 : String (1 .. 10) := "0123456789";
   V3 : String_Access;

end Test;