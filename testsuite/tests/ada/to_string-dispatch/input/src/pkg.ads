package Pkg is
   type T is tagged null record;

   function To_String (X : T) return String;

   procedure Print (X : T'Class);
end Pkg;
