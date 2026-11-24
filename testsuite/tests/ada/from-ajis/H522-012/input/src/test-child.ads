package Test.Child is

   --  Checks that the generated code still gets the dependency on GNAT.Stings
   --  even if it's mentionned in the parent.

   function Get return String_Access;

end Test.Child;