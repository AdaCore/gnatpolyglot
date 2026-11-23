package Test is

   type An_Array is array (Integer range <>) of Integer;
   type Access_An_Array is access all An_Array;

   function Sum (V : An_Array) return Integer;

   function F return An_Array;

   type T is record
      A : Integer;
   end record;

   type Enum is (A, B, C);

   type An_Enum_Array is array (Enum range <>, Boolean range <>) of T;

   V : An_Enum_Array (A .. C, True .. False);

end Test;