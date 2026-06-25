package Test is

   type Root is abstract tagged null record;

   type Root_Access is access all Root;

   procedure P1 (R : Root_Access);
   procedure P2 (R : in out Root_Access);
   function F1 return Root_Access;

end Test;
