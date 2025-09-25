package Test is

   type Rec is record
      I : Integer;
   end record;

   type Rec_Access is access all Rec;

   function Rec_F (A : Rec_Access) return Rec_Access;

   procedure Rec_P (A : Rec_Access);

   procedure Rec_In_Out (A : in out Rec_Access);

end Test;
