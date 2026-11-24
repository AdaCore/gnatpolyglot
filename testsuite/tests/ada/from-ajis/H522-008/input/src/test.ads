with Lib_2; use Lib_2;

package Test is

   --  The binded used to generate wrappers based on A_String_Access instead
   --  of An_Other_String_Access, which is problematic since Lib_1 is not
   --  withed. This test checks that this work fine now.

   function F (V : An_Other_String_Access) return An_Other_String_Access;

   function F_2 (V : An_Other_String_Access_2) return An_Other_String_Access_2;

end Test;