package Test is

   type T is private;

   function To_T (I : Integer) return T;

   function Add_T (L, R : T) return T;

   procedure Print (Val : T);

private

   type T is new Integer;

end Test;
