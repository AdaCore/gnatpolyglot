with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   Y : constant TA := new T'(null record);

   procedure Get (Acc : out TA) is
   begin
      Acc := Y;
   end Get;

   procedure Call (Cb : Callback) is
   begin
      Cb (Y);
   end Call;

   procedure Call_Out (Cb : Callback_Out) is
      Acc : TA;
   begin
      Cb (Acc);
      Put_Line (Boolean'Image (Acc = Y));
   end Call_Out;

   procedure Get_R (Acc : out RA) is
   begin
      Acc := X;
   end Get_R;

   procedure Call_R (Cb : Callback_R) is
   begin
      Cb (X);
   end Call_R;

   procedure Call_Out_R (Cb : Callback_Out_R) is
      Acc : RA;
   begin
      Cb (Acc);
      Put_Line (Acc.V'Image);
   end Call_Out_R;

end Test;
