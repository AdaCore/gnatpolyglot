package Test is

   type My_Positive is new Integer range 1 .. Integer'Last;

   type An_Array is array (My_Positive range <>) of Integer;

   V : An_Array (0 .. -1);

end Test;
