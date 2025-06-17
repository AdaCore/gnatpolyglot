with Ada.Strings; use Ada.Strings;

package Test is

   function String_Func return String;
   procedure String_Proc (Str : String);

   type User_Str is array (Positive range <>) of Character;

   function User_String_Func return User_Str;
   procedure User_String_Proc (Str : User_Str);

end Test;
