with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Increment_All (X : A) return A is
      Result : A;
   begin
      Result.V := X.V + 1;

      return Result;
   end Increment_All;

   procedure Print (X : A) is
   begin
      Put_Line (X.V'Img);
   end Print;

   function Increment_All (X : B) return B is
      Result : B;
   begin
      Result.V := X.V + 1;
      Result.V2 := X.V2 + 1;

      return Result;
   end Increment_All;

   procedure Print (X : B) is
   begin
      Put_Line (X.V'Img & ", " & X.V2'Img);
   end Print;

end Test;
