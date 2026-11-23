package Test is

   type Enum is (A, B, C);

   type An_Array is array (Enum) of Integer;

   procedure Print_Contents (Arr : An_Array);

end Test;
