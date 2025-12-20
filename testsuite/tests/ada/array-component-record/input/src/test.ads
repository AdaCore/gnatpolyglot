package Test is

   type Arr is array (Positive range <>) of Natural;

   type Inline_Vector is record
      Data : Arr (1 .. 10) := (others => 0);
      Size : Natural := 0;
   end record;

   function Get (V : Inline_Vector; I : Positive) return Integer
   is (if I < V.Size + 1 then V.Data (I) else -1);

   procedure Set (V : in out Inline_Vector; I : Positive; N : Natural);

   procedure Push (V : in out Inline_Vector; N : Natural);

   function Pop (V : in out Inline_Vector) return Integer;

   type Arr_Acc is access all Arr;

   type Vector is record
      Data           : Arr_Acc := null;
      Capacity, Size : Natural := 0;
   end record;

   function Get (V : Vector; I : Positive) return Integer
   is (if I < V.Size + 1 then V.Data.all (I) else -1);

   procedure Set (V : Vector; I : Positive; N : Natural);

   procedure Push (V : in out Vector; N : Natural);

   function Pop (V : in out Vector) return Integer;

   procedure Free (V : in out Vector);

end Test;
