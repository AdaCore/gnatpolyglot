with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function Clone_1 (V : C) return C is
   begin
      return V;
   end Clone_1;

   function Clone_2 (V : access C) return C is
   begin
      return V.all;
   end Clone_2;

   procedure Prim (V : C) is
   begin
      Put_Line ("PRIM C");
   end Prim;

   procedure Call (V : access C'Class) is
      W : C'Class := Clone_1 (V.all);
      X : C'Class := Clone_2 (V);
   begin
      if W in C then
         Put_Line ("C");
      else
         Put_Line ("NOT C");
      end if;

      if X in C then
         Put_Line ("C");
      else
         Put_Line ("NOT C");
      end if;

      Prim (W);
      Prim (X);
   end Call;

   function Clone_Not (V : C2) return C2 is
   begin
      return (V => not V.V);
   end Clone_Not;

   procedure Call2 (V : access C2'Class) is
      X : C2'Class := Clone_Not (V.all);
   begin
      Put_Line (X.V'Img);

      Prim (X);
   end Call2;

   procedure Prim (V : C2) is
   begin
      Put_Line ("PRIM C2");
   end Prim;

end Test;
