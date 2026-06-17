with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_Enum_1(V: Enum_1) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
   end P_Enum_1;

   procedure P_Enum_2(V: Enum_2) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
   end P_Enum_2;

   procedure P_Enum_Big(V: Enum_Big) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
   end P_Enum_Big;

   procedure P_In_Out(V: in out Enum_2) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
      V := Enum_2'Pred(V);
   end P_In_Out;

   function F_Enum_Big(V: Enum_Big) return Enum_Big is
   begin
      case V is
         when G => return H;
         when H => return I;
         when I => return G;
      end case;
   end F_Enum_Big;

   function F_Enum_1(V: Enum_1) return Enum_1 is
   begin
      case V is
         when A => return B;
         when B => return C;
         when C => return A;
      end case;
   end F_Enum_1;

   function F_Enum_2(V: Enum_2) return Enum_2 is
   begin
      case V is
         when D => return E;
         when E => return F;
         when F => return D;
      end case;
   end F_Enum_2;

end Test;

