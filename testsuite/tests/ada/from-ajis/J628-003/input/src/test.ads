package Test is

   type T is new Integer;

   function "/" (Left, Right : T) return T is abstract;

   function "*" (Left, Right : T) return T;

end Test;
