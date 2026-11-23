package Test is

   type Root is abstract tagged null record;

   type Other_Root is abstract tagged null record;

   procedure P (Self : Root; V : not null access Other_Root'Class);

end Test;
