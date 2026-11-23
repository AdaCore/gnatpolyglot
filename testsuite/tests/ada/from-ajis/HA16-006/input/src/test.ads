package Test is

   type Root is tagged null record;

   type Root_Access is access all Root'Class;

   function F (P1 : Root; P2 : String) return Root_Access;

   function Call_F (P1 : Root'Class; P2 : String) return Root_Access;

   procedure Create_Root (V : out Root_Access);

end Test;
