with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure P1 (X : in out T; F : Float) is
   begin
      Put_Line ("P1");
   end P1;

   procedure P (X : T'Class) is
   begin
      Put_Line ("P");
   end P;

   procedure Q1 (I : Integer; X : T) is
   begin
      Put_Line ("Q1");
   end Q1;

   procedure Q2 (I : Integer; X : T'Class) is
   begin
      Put_Line ("Q2");
   end Q2;

   function F return T is
      V : T;
   begin
      Put_Line ("F");
      return V;
   end F;

end Test;
