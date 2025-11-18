with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F (R : Root; P: Priv) return Priv is ((I => P.I - 1));

   function Inc (P: Priv) return Priv is ((I => P.I + 1));

   procedure Print (P: Priv) is
   begin
      Put_Line ("(" & P.I'Image & ")");
   end Print;

   function Call_F (R : Root'Class; P: Priv) return Priv is
   begin
      return R.F (P);
   end Call_F;
end Test;
