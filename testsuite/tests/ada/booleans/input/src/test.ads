package Test is
   function Is_Even (I : Integer) return Boolean;

   procedure Is_Odd (B: in out Boolean; I : Integer);

   procedure Print_Bool (B : Boolean);

   type My_Bool is new Boolean;

   procedure Print_My_Bool (B : My_Bool);

end Test;
