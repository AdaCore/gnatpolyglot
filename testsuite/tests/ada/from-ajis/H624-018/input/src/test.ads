package Test is

   type Int_R is range 1 .. 10;

   type R is record
      V : Int_R;
   end record;

   function Get_New_R (V : Int_R) return R;

   type Int_R_Tagged is range 11 .. 20;

   type R_Tagged is tagged record
      V : Int_R_Tagged;
   end record;

   function Get_New_R_Tagged (V : Int_R_Tagged) return R_Tagged'Class;

end Test;
