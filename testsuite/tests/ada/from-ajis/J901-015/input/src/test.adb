with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;

package body Test is

   function Remove_Address (Str : String) return String is
   begin
      for J in Str'Range loop
         if Str (J) = ']' then
            return Str (J + 2 .. Str'Last);
         end if;
      end loop;

      return Str;
   end Remove_Address;

   procedure Test_All (V : T'Class) is
   begin
      declare
         Loc : R;
      begin
         Loc := V.F1;
         Put_Line ("[1] OK");
      exception
         when E : others =>
            Put_Line ("[1] NOK");
            Put_Line (Remove_Address (Exception_Message (E)));
      end;

      declare
         Loc : A_R;
      begin
         Loc := V.F2;
         Put_Line ("[2] OK");
      exception
         when E : others =>
            Put_Line ("[2] NOK");
            Put_Line (Remove_Address (Exception_Message (E)));
      end;

      declare
         Loc : A_R;
      begin
         V.P (Loc);
         Put_Line ("[3] OK");
      exception
         when E : others =>
            Put_Line ("[3] NOK");
            Put_Line (Remove_Address (Exception_Message (E)));
      end;
   end Test_All;

   procedure Call_F (F : Ptr_F) is
      V : R;
   begin
      V := F.all;
   exception
      when E : others =>
         Put_Line ("[4] NOK");
         Put_Line (Remove_Address (Exception_Message (E)));
   end Call_F;

   procedure Call_P (P : Ptr_P) is
      V : A_R;
   begin
      P.all (V);
   exception
      when E : others =>
         Put_Line ("[4] NOK");
         Put_Line (Remove_Address (Exception_Message (E)));
   end Call_P;

end Test;
