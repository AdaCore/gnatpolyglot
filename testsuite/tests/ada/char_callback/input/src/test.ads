package Test is
   type Char_Callback is access procedure
     (V1 : out Character;
      V2 : in out Character);

   procedure Call (C : Char_Callback);
end Test;
