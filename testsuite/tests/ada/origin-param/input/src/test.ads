package Test is

   type Not_Int is private;

   function Get return Not_Int;

private

   type Not_Int is new Integer;

   function Get return Not_Int is (0);

end Test;
