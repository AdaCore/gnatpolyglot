with Text_IO; use Text_IO;

package body Fixeds is

   function F_Delta (D : My_Delta; M : Integer) return My_Delta is
      Res : My_Delta := D + Small * M;
   begin
      Put_Line (D'Image);
      Put_Line (Res'Image);
      return Res;
   end F_Delta;

   procedure F_Decimal (D : Decimal) is
   begin
      Put_Line (D'Image);
   end F_Decimal;

end Fixeds;
