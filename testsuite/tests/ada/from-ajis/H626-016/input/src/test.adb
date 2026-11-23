package body Test is

   procedure Create_String (S : out String_Access) is
   begin
      S := new String'("IntialString");
   end Create_String;

   procedure Create_And_Append_String (S : in out String_Access) is
   begin
      S := new String'(S.all & " AppenedString");
   end Create_And_Append_String;

end Test;