package Test is

   type String_Access is access all String;

   function Return_String return String_Access;

end Test;
