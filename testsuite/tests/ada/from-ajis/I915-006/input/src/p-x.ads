package P.X is

   type T is null record;

   procedure Proc (P : T);

   procedure Proc_2 (P : access T);

   P : T;

end P.X;
