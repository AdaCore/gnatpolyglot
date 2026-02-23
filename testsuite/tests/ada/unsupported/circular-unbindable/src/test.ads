with Types; use Types;

package Test is

   -- Check the use of the record first
   procedure P1 (R : Rec1) is null;
   -- Then the use of the access
   procedure P2 (R : Rec1_Acc) is null;

   -- Check the use of the access first
   procedure P3 (R : Rec2_Acc) is null;
   -- Then the use of the record
   procedure P4 (R : Rec2) is null;

   -- Check the use of the record first
   procedure P5 (R : Rec_M11) is null;
   -- Then the use of the access
   procedure P6 (R : Rec_M12_Acc) is null;

   -- Check the use of the access first
   procedure P7 (R : Rec_M21) is null;
   -- Then the use of the record
   procedure P8 (R : Rec_M21_Acc) is null;

end Test;
