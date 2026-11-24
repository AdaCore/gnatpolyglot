package body Test.Child is

   function Get return String_Access is
   begin
      return new String'("TEST FROM CHILD");
   end Get;

end Test.Child;