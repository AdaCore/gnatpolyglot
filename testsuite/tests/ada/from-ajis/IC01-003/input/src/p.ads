package P is

   type Root_1 (V : Integer) is tagged record
      F : Integer;
   end record;

   type Child_1_3 is new Root_1 (1) with private;

private

   type Child_1_3 is new Root_1 (1) with null record;

end P;
