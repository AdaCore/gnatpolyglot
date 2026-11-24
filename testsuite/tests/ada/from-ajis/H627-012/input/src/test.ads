package Test is

   type A_Type is abstract tagged limited null record;

   type A_Child is new A_Type with record
      X : Integer;
   end record;

end Test;
