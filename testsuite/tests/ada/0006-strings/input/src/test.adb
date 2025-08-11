with Ada.Strings; use Ada.Strings;
with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function String_Func return String is
   begin
      return "String_Func";
   end String_Func;

   procedure String_Proc (Str : String) is
   begin
      Put_Line ("Printing Ada.String: " & Str);
   end String_Proc;

   function User_String_Func return User_Str is
   begin
      return "User_String";
   end User_String_Func;

   procedure User_String_Proc (Str : User_Str) is
   begin
      Put_Line ("Printing Test.User_String: " & String (Str));
   end User_String_Proc;

end Test;
