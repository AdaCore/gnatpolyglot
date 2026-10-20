package Test is

   type Parent is tagged null record;

   type Child is new Parent with null record;

   type Parent_Access is access all Parent;

   type Child_Access is access all Child;

   type Parent_Cst is access constant Parent;

   function Get_Child return Child_Access;

   procedure Print (P : Parent_Cst);

end Test;
