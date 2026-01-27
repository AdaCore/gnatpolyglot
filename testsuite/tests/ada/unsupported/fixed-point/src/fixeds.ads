package Fixeds is

   Small : constant := 0.0000001;
   type My_Delta is delta Small digits 11 range 0.0 .. 100.0;

   function F_Delta (D : My_Delta; M : Integer) return My_Delta;

   type Decimal is delta 0.1 digits 4;

   procedure F_Decimal (D : Decimal);

end Fixeds;
