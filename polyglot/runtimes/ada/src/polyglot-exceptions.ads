with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;
with Ada.Exceptions; use Ada.Exceptions;
with System;

with Polyglot; use Polyglot;
with Polyglot.Ada.Strings;

package Polyglot.Exceptions is

   procedure Exc_Free is new
     Standard.Ada.Unchecked_Deallocation
       (Exception_Occurrence,
        Exception_Occurrence_Access);

   function Exc_To_Addr is new
     Standard.Ada.Unchecked_Conversion
       (Exception_Occurrence_Access,
        System.Address);

   function Addr_To_Exc is new
     Standard.Ada.Unchecked_Conversion
       (System.Address,
        Exception_Occurrence_Access);

   type Identify_Exception_Type is
     access function (Id : Exception_Id) return Interfaces.C.Int;

   procedure Raise_Exception
     (K        : Kernel_Access;
      Exc      : Exception_Occurrence_Access;
      Identify : Identify_Exception_Type);

   procedure Clear_Last_Exception (K : Kernel_Access);
   pragma Export (C, Clear_Last_Exception);

end Polyglot.Exceptions;
