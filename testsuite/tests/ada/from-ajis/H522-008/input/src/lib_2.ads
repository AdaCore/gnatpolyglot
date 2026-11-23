with Lib_1; use Lib_1;

package Lib_2 is

   subtype An_Other_String_Access is A_String_Access;

   type An_Other_String_Access_2 is new A_String_Access;

end Lib_2;