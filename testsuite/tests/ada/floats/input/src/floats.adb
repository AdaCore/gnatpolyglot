with Text_IO; use Text_IO;

package body Floats is

   function F_Short return Short_Float is
   begin
      return 0.5;
   end F_Short;

   function F_Float return Float is
   begin
      return 0.25;
   end F_Float;

   function F_Long_Float return Long_Float is
   begin
      return 0.125;
   end F_Long_Float;

   function F_Long_Long_Float return Long_Long_Float is
   begin
      return 2.5;
   end F_Long_Long_Float;

   function F_My_Float return My_Float is
   begin
      return 10.025;
   end F_My_Float;

   function F_My_New_Short return My_New_Short is
      Res : My_New_Short := 0.123;
   begin
      return (Res);
   end F_My_New_Short;

   function F_My_Long_Float return My_Long_Float is
   begin
      return 1000.25;
   end F_My_Long_Float;

   function F_My_Small return My_Small is
      Res : My_Small := -0.123;
   begin
      return (Res);
   end F_My_Small;

   function Identity(F: Float) return Float is
   begin
      Put_Line (F'Image);
      return F;
   end Identity;

end Floats;
