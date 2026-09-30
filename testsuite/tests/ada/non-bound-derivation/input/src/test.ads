package Test is
   type Root is tagged null record;
   type Child is new Root with null record;
   type G_Child is new Child with null record;

   function Get_Object_1 return Root'Class;
   function Get_Object_2 return Root'Class;
end Test;
