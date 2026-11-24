package Test is

   type A_S is access all String;

   function Return_A_String_Access return access String;

   procedure Display_A_String_Access (Str : access String);

end Test;