package Test is

   type T is tagged null record;

   type TA is access constant T;

   function Id (Acc : TA) return TA is (Acc);

end Test;
