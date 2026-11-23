with Lib_1; use Lib_1;

package body Test is

   function F (V : An_Other_String_Access) return An_Other_String_Access is
   begin
      if V /= null then
         return new String'("FOUND SOMETHING");
      else
         return new String'("FOUND NOTING");
      end if;
   end F;

   function F_2
     (V : An_Other_String_Access_2) return An_Other_String_Access_2
   is
   begin
      if V /= null then
         return new String'("FOUND SOMETHING");
      else
         return new String'("FOUND NOTING");
      end if;
   end F_2;

end Test;