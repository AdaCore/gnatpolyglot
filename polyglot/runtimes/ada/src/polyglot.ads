pragma Interrupt_State (SIGSEGV, System);
with GNAT.Threads;
-- On GNU/Linux, the GNAT runtime installs a SIGSEGV signal handler, so turn
-- memory issues into `Storage_Error` exceptions. This may be incompatible with
-- the runtime of other languages, such as C++'s.

with System;

with Interfaces.C;
with Interfaces.C.Strings;

package Polyglot is

   type Kernel;
   type Kernel_Access is access Kernel with Size => Standard'Address_Size;

   type Clear_Exception_Type is access procedure (K : Kernel_Access)
   with Convention => C;

   type Exception_Information is record
      Exception_Id      : Interfaces.C.int;
      Message           : Interfaces.C.Strings.chars_ptr;
      Current_Exception : System.Address;
      Clear_Exception   : Clear_Exception_Type;
   end record
   with Convention => C;

   type Kernel is record
      Exc_Info : Exception_Information;
   end record
   with Convention => C;

   function Get_Kernel return Kernel_Access;

end Polyglot;

