package body Test is

   function Raise_Exception return Integer is
   begin
      if True then
         raise Constraint_Error;
      end if;

      return 0;
   end Raise_Exception;

end Test;
