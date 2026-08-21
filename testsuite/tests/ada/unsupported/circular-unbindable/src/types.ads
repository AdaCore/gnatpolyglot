package Types is

   -- Simple recursive record 1

   type Unbindable1 is access Integer;

   type Rec1;
   type Rec1_Acc is access all Rec1;

   type Rec1 is record
      Self : Rec1_Acc;
      Err : Unbindable1;
   end record;

   -- Simple recursive record 2

   type Unbindable2 is access Integer;

   type Rec2;
   type Rec2_Acc is access all Rec2;

   type Rec2 is record
      Self : Rec2_Acc;
      Err : Unbindable2;
   end record;

   -- Mutually recursive records 1

   type Unbindable3 is access Integer;

   type Rec_M11;
   type Rec_M11_Acc is access all Rec_M11;

   type Rec_M12;
   type Rec_M12_Acc is access all Rec_M12;

   type Rec_M11 is record
      Self : Rec_M12_Acc;
      Err : Unbindable2;
   end record;

   type Rec_M12 is record
      Self : Rec_M11_Acc;
      Err : Unbindable3;
   end record;

   -- Mutually recursive records 2

   type Unbindable4 is access Integer;

   type Rec_M21;
   type Rec_M21_Acc is access all Rec_M21;

   type Rec_M22;
   type Rec_M22_Acc is access all Rec_M22;

   type Rec_M21 is record
      Self : Rec_M22_Acc;
      Err : Unbindable2;
   end record;

   type Rec_M22 is record
      Self : Rec_M21_Acc;
      Err : Unbindable4;
   end record;

end Types;
