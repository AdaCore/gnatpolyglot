package body Test is

   function F (V : A_String_Access) return A_String_Access is
   begin
      if V /= null then
         return new String'("FOUND SOMETHING");
      else
         return new String'("FOUND NOTING");
      end if;
   end F;

end Test;
