package Test is

   type String_Access is access all String;

   procedure Create_String (S : out String_Access);

   procedure Create_And_Append_String (S : in out String_Access);

end Test;