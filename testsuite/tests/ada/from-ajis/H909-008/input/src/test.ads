package Test is

   type An_Array is array (Integer range <>) of Integer;
   type An_Array_Access is access all An_Array;

   V : An_Array := (1 => 1, 2 => 2);
   V2 : An_Array_Access := new An_Array'(1 => 3, 2 => 4);

   type R is record
      F : An_Array (1 .. 2) := (1 => 5, 2 => 6);
      F2 : An_Array_Access := new An_Array'(1 => 7, 2 => 8);
   end record;

   procedure Print1 (V : An_Array);

   procedure Print2 (V : An_Array_Access);

   procedure Print3 (V : An_Array_Access);

end Test;
