with Ada.Unchecked_Conversion;

package body Polyglot is

   ----------------
   -- Get_Kernel --
   ----------------

   function Get_Kernel return Kernel_Access is

      function polyglot_get_kernel return System.Address
      with
        Import        => True,
        Convention    => C,
        External_Name => "polyglot_get_kernel";
      function Addr_To_Kernel is new
        Ada.Unchecked_Conversion (System.Address, Kernel_Access);
   begin
      return Addr_To_Kernel (polyglot_get_kernel);
   end Get_Kernel;

end Polyglot;
