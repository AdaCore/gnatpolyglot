package Test is

   type Enum is (E1, E2, E3);

   type Small_Rec is record
      F1, F2 : Enum;
   end record;
   pragma Pack (Small_Rec);

   type Small_Rec_Access is access all Small_Rec;

   type Rec is record
      A : Boolean;
      B : Boolean;
      C : Enum;
      D : Small_Rec;
      E : Small_Rec_Access;
   end record;
   pragma Pack (Rec);

   type An_Array is array (Integer range <>) of Small_Rec;

   Global_Var : Small_Rec;

end Test;