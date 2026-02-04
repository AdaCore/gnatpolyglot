with Ada.Unchecked_Deallocation;

package Test is

   type Rec is record
      I : Integer;
   end record;

   type Rec_Access is access all Rec;

   procedure Free is new Ada.Unchecked_Deallocation (Rec, Rec_Access);

   type Arr is array (Positive range <>) of Integer;

   type Arr_Access is access all Arr;

   function Rec_F (A : Rec_Access) return Rec_Access;

   procedure Rec_P (A : Rec_Access);

   procedure Rec_In_Out (A : in out Rec_Access);

   function Arr_F (A : Arr_Access) return Arr_Access;

   procedure Arr_P (A : Arr_Access);

   procedure Arr_P_In_Out (A : in out Arr_Access);
end Test;
