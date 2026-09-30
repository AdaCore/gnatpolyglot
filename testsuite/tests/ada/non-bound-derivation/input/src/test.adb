package body Test is
   type Hidden_Child is new Child with null record;
   type Hidden_GChild is new G_Child with null record;

   function Get_Object_1 return Root'Class is
   begin
      return Hidden_Child'(null record);
   end Get_Object_1;

   function Get_Object_2 return Root'Class is
   begin
      return Hidden_GChild'(null record);
   end Get_Object_2;

end Test;

