--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Ada.Unchecked_Conversion;
with Ada.Unchecked_Deallocation;
with Ada.Exceptions; use Ada.Exceptions;
with Interfaces.C.Strings;
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

   function Identify_Standard_Exception
     (Id : Exception_Id) return Interfaces.C.int;

   procedure Raise_Exception
     (Exc : Exception_Occurrence; Identify : Identify_Exception_Type);

   procedure Clear_Last_Exception (K : Kernel_Access);
   pragma Export (C, Clear_Last_Exception);

   type Standard_Exception_Kind is
     (Constraint_Error_Kind,
      Program_Error_Kind,
      Storage_Error_Kind,
      Tasking_Error_Kind)
   with Convention => C;

   for Standard_Exception_Kind use
     (Constraint_Error_Kind => -4,
      Program_Error_Kind    => -3,
      Storage_Error_Kind    => -2,
      Tasking_Error_Kind    => -1);

   function Create_Exception_Occurence
     (Kind : Standard_Exception_Kind) return System.Address;
   pragma
     Export
       (C,
        Create_Exception_Occurence,
        "polyglot__ada__exceptions__create_exception_occurence");

   function Create_Exception_Occurence_Message
     (Kind : Standard_Exception_Kind;
      Msg  : Polyglot.Ada.Strings.Polyglot_String) return System.Address;
   pragma
     Export
       (C,
        Create_Exception_Occurence_Message,
        "polyglot__ada__exceptions__create_exception_occurence_message");

   procedure Free_Exception_Occurence (Addr : System.Address);
   pragma
     Export
       (C,
        Free_Exception_Occurence,
        "polyglot__ada__exceptions__free_exception_occurence");

   function Get_Cstr_Message
     (Addr : System.Address) return Interfaces.C.Strings.chars_ptr;
   pragma
     Export
       (C,
        Get_Cstr_Message,
        "polyglot__ada__exceptions__get_cstr_message");

end Polyglot.Exceptions;
