package Test is

   type Root (<>) is tagged private;

   function Get_I (V : Root) return Integer;

   function Get_R return Root'Class;

private

   type Root is tagged null record;

end Test;
