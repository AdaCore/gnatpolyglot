with Parent; use Parent;

package Test is

   --  This test check that Rec is correctly bound, even if it's referenced
   --  from an access type.

   X : Rec_Access;

end Test;