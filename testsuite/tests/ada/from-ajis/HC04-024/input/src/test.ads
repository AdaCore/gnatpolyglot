with Ada.Finalization; use Ada.Finalization;

package Test is

   type Rec is new Controlled with null record;

   overriding
   procedure Initialize (R : in out Rec);

   type Rec_Acc is access all Rec;

   type P_Acc is access procedure (V : in out Rec_Acc);

   procedure Strange (V : P_Acc);

   type Rec2 is null record;

   type Rec2_Acc is access all Rec2;

   type P_Acc2 is access procedure (V : in out Rec2_Acc);

   procedure Strange2 (V : P_Acc2);

   type Rec3 is new Controlled with null record;

   overriding
   procedure Initialize (R : in out Rec3);

   type Rec3_Acc is access all Rec3;

   type P_Acc3 is access procedure (V : out Rec3_Acc);

   procedure Strange3 (V : P_Acc3);

   type Rec4 is null record;

   type Rec4_Acc is access all Rec4;

   type P_Acc4 is access procedure (V : out Rec4_Acc);

   procedure Strange4 (V : P_Acc4);

end Test;
