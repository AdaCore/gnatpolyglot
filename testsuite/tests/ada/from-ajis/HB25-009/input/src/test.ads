with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Rec is tagged record
      null;
   end record;

   procedure P_1 (V : Rec);

   procedure Call_P1 (Dummy : Boolean; V : Rec'Class);

end Test;