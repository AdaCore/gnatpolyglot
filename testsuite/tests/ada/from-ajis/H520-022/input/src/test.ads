package Test is

   type String_Access is access all String;

   type String_Array is array (Integer range <>) of String_Access;

   String_Repo : String_Array (1 .. 2) :=
     (new String'("This is the first string"),
      new String'("This is the second string"));

end Test;