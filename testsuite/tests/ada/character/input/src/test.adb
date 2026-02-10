with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P_Character(V: Character) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
   end P_Character;

   procedure P_Alphabet(V: Alphabet) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
   end P_Alphabet;

   procedure P_In_Out(V: in out Alphabet) is
   begin
      Put_Line ("Got " & V'Image & " =" & V'Enum_Rep'Image);
      V := Alphabet'Pred(V);
   end P_In_Out;

   function F_Character(V: Character) return Character is
   begin
      return Character'Succ(V);
   end F_Character;

   function F_Alphabet(V: Alphabet) return Alphabet is
   begin
      case V is
         when 'z' => return 'a';
         when others => return Alphabet'Succ(V);
      end case;
   end F_Alphabet;

end Test;

