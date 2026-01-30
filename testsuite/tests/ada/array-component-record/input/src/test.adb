with Ada.Text_IO; use Ada.Text_IO;
with Ada.Unchecked_Deallocation;

package body Test is

   procedure Set (V : in out Inline_Vector; I : Positive; N : Natural) is
   begin
      if I < V.Size + 1 then
         V.Data (I) := N;
      end if;
   end Set;

   procedure Push (V : in out Inline_Vector; N : Natural) is
   begin
      if V.Size < 10 then
         V.Data (V.Size + 1) := N;
         V.Size := V.Size + 1;
      end if;
   end Push;

   function Pop (V : in out Inline_Vector) return Integer is
   begin
      return Ret : Integer := Get (V, V.Size + 1) do
         if V.Size > 0 then
            V.Size := V.Size - 1;
         end if;
      end return;
   end Pop;

   procedure Free is new Ada.Unchecked_Deallocation (Arr, Arr_Acc);

   procedure Set (V : Vector; I : Positive; N : Natural) is
   begin
      if I < V.Size + 1 then
         V.Data (I) := N;
      end if;
   end Set;

   procedure Push (V : in out Vector; N : Natural) is
      Data : Arr_Acc := V.Data;
   begin
      if V.Capacity = 0 then
         V.Capacity := 1;
         Data := new Arr (1 .. V.Capacity);
         V.Data := Data;
      elsif V.Size = V.Capacity then
         V.Capacity := V.Capacity * 2;
         Data := new Arr (1 .. V.Capacity);
         Data.all (1 .. V.Size) := V.Data.all (1 .. V.Size);
         Data.all (V.Size + 1 .. Data.all'Last) := (others => 0);
         Free (V.Data);
         V.Data := Data;
      end if;
      Data (V.Size + 1) := N;
      V.Size := V.Size + 1;
   end Push;

   function Pop (V : in out Vector) return Integer is
   begin
      return Ret : Integer := Get (V, V.Size + 1) do
         if V.Size > 0 then
            V.Size := V.Size - 1;
         end if;
      end return;
   end Pop;

   procedure Free (V : in out Vector) is
   begin
      Free (V.Data);
   end Free;

end Test;
