package Floats is

   function F_Short return Short_Float;
   function F_Float return Float;
   function F_Long_Float return Long_Float;

   type My_Float is digits 6 range -(2.0**20) .. (2.0**20);
   type My_New_Short is new Short_Float;
   subtype My_Long_Float is Long_Float;
   type My_Small is digits 4 range -256.0 .. 0.0;

   function F_My_Float return My_Float;
   function F_My_New_Short return My_New_Short;
   function F_My_Long_Float return My_Long_Float;
   function F_My_Small return My_Small;

   function Inc (F: Float) return Float is (F + 1.0);

   function Identity(F: Float) return Float;

end Floats;
