package Test is

   procedure Call;

   type Callback is access procedure;

   type Rec is record
       C : Callback := Call'Access;
   end record;

   type Arr is array (Positive range <>) of Callback;

   function Foo return Arr is (Call'Access, Call'Access, Call'Access);

end Test;
