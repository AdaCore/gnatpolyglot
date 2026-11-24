package Test is

   type T is private;

   subtype S is T;

   procedure P (V : S);

private

   type T is null record;

end Test;
