package body Test is

   function Make_Fixed return Fixed is ((1, 2, 3));
   procedure Take_Fixed (Value : Fixed) is null;
   function Make_Dynamic return Dynamic is ((others => 0));
   function Make_Derived return Derived is ((1, 2, 3));
   function Make_Derived_From_Window return Derived_From_Window is ((1, 2, 3));

end Test;
