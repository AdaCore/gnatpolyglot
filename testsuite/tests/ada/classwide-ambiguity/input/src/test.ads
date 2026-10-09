package Test is

   type T is tagged null record;

   function Make return T
   is (null record);
   function Make return T'Class
   is (T'(null record));

   package Nested is

      procedure Foo(A: T) is null;
      procedure Foo(A: T'Class) is null;

   end Nested;

   function "=" (A, B : T'Class) return Boolean
   is (True);

end Test;
