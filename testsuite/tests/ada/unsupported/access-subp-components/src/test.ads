package Test is

   type Callback is access procedure;

   type Arr is array (Positive range <>) of Callback;

   type Rec is record
       C : Callback;
   end record;

end Test;
