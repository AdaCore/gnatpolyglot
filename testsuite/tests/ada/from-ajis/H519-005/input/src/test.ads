package Test is

   type String_Access is access all String;

   function Return_A_String_Access return String_Access;

   procedure Display_A_String_Access (Str : String_Access);

end Test;
