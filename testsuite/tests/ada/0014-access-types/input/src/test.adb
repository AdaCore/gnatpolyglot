with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   function Rec_F (A : Rec_Access) return Rec_Access is
      Ret : Rec_Access := new Rec'(A.all);
   begin
      A.I := A.I + 1;
      return Ret;
   end Rec_F;

   Escaped_Rec : Rec_Access := null;

   procedure Rec_P (A : Rec_Access) is
   begin
      if A /= null then
         Escaped_Rec := A;
         A.I := A.I + 1;
      elsif Escaped_Rec /= null then
         Escaped_Rec.I := Escaped_Rec.I + 1;
      end if;
   end Rec_P;

   procedure Rec_In_Out (A : in out Rec_Access) is
      New_A : Rec_Access := new Rec'(I => A.I * 2);
      procedure Free is new Ada.Unchecked_Deallocation (Rec, Rec_Access);
   begin
      Free (A);
      A := New_A;
   end Rec_In_Out;

end Test;
