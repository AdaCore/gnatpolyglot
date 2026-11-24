package body Test is

   function F (P1 : Root; P2 : String) return Root_Access is
   begin
      return null;
   end F;

   function Call_F (P1 : Root'Class; P2 : String) return Root_Access is
   begin
      return F (P1, P2);
   end Call_F;

   procedure Create_Root (V : out Root_Access) is
   begin
      V := new Root;
   end Create_Root;

end Test;
