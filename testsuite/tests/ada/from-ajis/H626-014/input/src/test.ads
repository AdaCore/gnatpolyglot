package Test is

   type Rec is record
      V : Integer;
   end record;

   type F_Acc is access function (R : Rec) return String;

   procedure Print_F_Acc (R : Rec; F : F_Acc);

   type Rec_Tagged is tagged record
      null;
   end record;

   function F (V : Rec_Tagged; X : Rec) return String;

end Test;