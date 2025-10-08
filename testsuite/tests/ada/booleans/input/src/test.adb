with Text_IO; use Text_IO;

package body Test is

   function Is_Even (I : Integer) return Boolean is 
   begin
      return I mod 2 = 0;
   end Is_Even;

   procedure Is_Odd (B: in out Boolean; I : Integer) is
   begin
      B := I mod 2 = 1;
   end Is_Odd;

   procedure Print_Bool (B : Boolean) is
   begin
      Put_Line (B'Image);
   end Print_Bool;

   procedure Print_My_Bool (B : My_Bool) is
   begin
      Put_Line (B'Image);
   end Print_My_Bool;

end Test;
