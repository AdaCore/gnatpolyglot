package Test is

   type Lim is limited record
      I : Integer;
   end record;

   type Value is limited record
      V : Lim;
   end record;

   function Get_Value return Value is (V => (I => 2));

   type Has_Default is limited record
      Def : Lim := (I => 1);
   end record;

end Test;
