package Test is

   type String_Access is access all String;

   function Create_String (Is_Null : Boolean) return String_Access;

   procedure Print_String_Access (Str : String_Access);

   procedure Print_String (Str : String);

end Test;