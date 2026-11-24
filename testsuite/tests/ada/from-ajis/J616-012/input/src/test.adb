package body Test is

   function Get_I (V : Root) return Integer is
   begin
      return 2;
   end Get_I;

   function Get_R return Root'Class is
   begin
      return Root'(null record);
   end Get_R;

end Test;
