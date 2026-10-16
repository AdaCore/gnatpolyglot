package Test is

   type T is tagged null record;

   type TA is access constant T;

   function Id (Acc : TA) return TA is (Acc);

   procedure Get (Acc : out TA);

   type Callback is access procedure (Acc : TA);

   procedure Call (Cb : Callback);

   type Callback_Out is access procedure (Acc : out TA);

   procedure Call_Out (Cb : Callback_Out);

   --  Same for a non-tagged type, which does not require identification

   type R is record
      V : Integer;
   end record;

   type RA is access constant R;

   function Id_R (Acc : RA) return RA is (Acc);

   procedure Get_R (Acc : out RA);

   type Holder is record
      Acc : RA;
   end record;

   X : RA := new R'(V => 2);

   type Callback_R is access procedure (Acc : RA);

   procedure Call_R (Cb : Callback_R);

   type Callback_Out_R is access procedure (Acc : out RA);

   procedure Call_Out_R (Cb : Callback_Out_R);

end Test;
