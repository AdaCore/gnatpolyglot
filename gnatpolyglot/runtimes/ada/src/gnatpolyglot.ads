--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

pragma Interrupts_System_By_Default;
-- On GNU/Linux, the GNAT runtime installs a SIGSEGV signal handler, so turn
-- memory issues into `Storage_Error` exceptions. This may be incompatible with
-- the runtime of other languages, such as C++'s.

with System;

with Interfaces.C;
with Interfaces.C.Strings;

package GNATpolyglot is

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

   type Callback_Data is record
      Addr : System.Address;
      -- The address of the function to call
      Data : System.Address;
      -- Opaque data to give as first parameter of the call to Callback_Addr.
      Extra : System.Address;
      -- Additional opaque data. This data is passed to dispatching functions
      -- called from the vtable. Its goal is to hold values meant to help with
      -- upcalling to the user implementations (e.g. hold a reference to a Java
      -- VM).
   end record
   with Convention => C_Pass_By_Copy;

end GNATpolyglot;

