package Test  is

   type R is record
      V : Integer;
   end record;

   type R_Acc is access all R;

   type P_Acc is access function (X : in out R_Acc) return Integer;

   -- Upcall callback
   function Call_P (X : P_Acc) return Integer;

   type T is tagged null record;

   -- Upcall dynamic dispatch
   function P (V : T; Rec : in out R_Acc) return Integer is (0);
   function Call_P (V : T'Class) return Integer;

end Test;
