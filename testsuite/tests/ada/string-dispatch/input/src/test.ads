package Test is

   type T is tagged null record;

   function Concat (Obj : T; A, B : String) return String
   is (A & B);

   function Call_Concat (Obj : T'Class; A, B : String) return String
   is (Obj.Concat (A, B));

end Test;
