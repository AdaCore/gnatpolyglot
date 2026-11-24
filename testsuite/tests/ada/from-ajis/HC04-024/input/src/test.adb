with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   procedure Initialize (R : in out Rec) is
   begin
      Put_Line ("INIT [1]");
   end Initialize;

   procedure Strange (V : P_Acc) is
      X : Rec_Acc := null;
   begin
      V.all (X);
      if X = null then
         Put_Line ("[ADA - 1] NULL");
      else
         Put_Line ("[ADA - 1] NOT NULL");
      end if;
   end Strange;

   procedure Strange2 (V : P_Acc2) is
      X : Rec2_Acc := null;
   begin
      V.all (X);
      if X = null then
         Put_Line ("[ADA - 2] NULL");
      else
         Put_Line ("[ADA - 2] NOT NULL");
      end if;
   end Strange2;

   procedure Initialize (R : in out Rec3) is
   begin
      Put_Line ("INIT [3]");
   end Initialize;

   procedure Strange3 (V : P_Acc3) is
      X : Rec3_Acc := new Rec3;
   begin
      V.all (X);
      if X = null then
         Put_Line ("[ADA - 3] NULL");
      else
         Put_Line ("[ADA - 3] NOT NULL");
      end if;
   end Strange3;

   procedure Strange4 (V : P_Acc4) is
      X : Rec4_Acc := new Rec4;
   begin
      V.all (X);
      if X = null then
         Put_Line ("[ADA - 4] NULL");
      else
         Put_Line ("[ADA - 4] NOT NULL");
      end if;
   end Strange4;

end Test;