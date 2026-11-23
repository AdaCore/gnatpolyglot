package Test is

   type T_1 is tagged record
      Val : Positive;
   end record;

   type Int_1 is new Integer;

   type Int_2 is new Integer;

   function F (X : T_1) return Int_1;

   function F (X : T_1) return Int_2;

   type T_2 is new T_1 with null record;

   type T_3 is new T_2 with null record;

end Test;
