package Test is

   type Fixed is array (1 .. 3) of Integer;
   function Make_Fixed return Fixed;
   procedure Take_Fixed (Value : Fixed);

   Size : Positive := 3;
   type Dynamic is array (1 .. Size) of Integer;
   function Make_Dynamic return Dynamic;

   type Unconstrained_Array is array (Positive range <>) of Integer;
   type Derived is new Unconstrained_Array (1 .. 3);
   function Make_Derived return Derived;

   Object : Unconstrained_Array (1 .. 3) := (1, 2, 3);
   type Container is record
      Component : Unconstrained_Array (3 .. 4) := (7, 8);
   end record;

   subtype Window is Unconstrained_Array (1 .. 3);
   Window_Object : Window := (1, 2, 3);
   type Derived_From_Window is new Window;
   function Make_Derived_From_Window return Derived_From_Window;

   type Derived_Unconstrained is new Unconstrained_Array;
   function Echo (Value : Derived_Unconstrained) return Derived_Unconstrained
     is (Value);

end Test;
