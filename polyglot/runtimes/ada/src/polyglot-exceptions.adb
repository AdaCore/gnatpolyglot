--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Interfaces.C.Strings; use Interfaces.C.Strings;

package body Polyglot.Exceptions is

   -----------------------------------
   -- Identify_Standard_Exceptions  --
   -----------------------------------

   function Identify_Standard_Exception
     (Id : Exception_Id) return Interfaces.C.int is
   begin
      if Id = Constraint_Error'Identity then
         return Interfaces.C.int (-4);
      elsif Id = Program_Error'Identity then
         return Interfaces.C.int (-3);
      elsif Id = Storage_Error'Identity then
         return Interfaces.C.int (-2);
      elsif Id = Tasking_error'Identity then
         return Interfaces.C.int (-1);
      end if;
      return Interfaces.C.int (0);
   end Identify_Standard_Exception;

   ---------------------
   -- Raise_Exception --
   ---------------------

   procedure Raise_Exception
     (Exc : Exception_Occurrence; Identify : Identify_Exception_Type)
   is
      K     : Kernel_Access := Polyglot.Get_Kernel;
      Id    : Exception_Id := Exception_Identity (Exc);
      Exc_A : Exception_Occurrence_Access :=
        Standard.Ada.Exceptions.Save_Occurrence (Exc);
   begin
      -- Make sure to free any uncaught exceptions.
      Clear_Last_Exception (K);
      K.Exc_Info.Exception_Id := Identify (Id);
      K.Exc_Info.Current_Exception := Exc_To_Addr (Exc_A);
      K.Exc_Info.Message :=
        Interfaces.C.Strings.New_String (Exception_Message (Exc));
      -- Provide the caller a handler to clear the exception when it catches it
      K.Exc_Info.Clear_Exception := Clear_Last_Exception'Access;
   end Raise_Exception;

   --------------------------
   -- Clear_Last_Exception --
   --------------------------

   procedure Clear_Last_Exception (K : Kernel_Access) is
      Last_Exc_Access : Exception_Occurrence_Access :=
        Addr_To_Exc (K.Exc_Info.Current_Exception);
   begin
      Exc_Free (Last_Exc_Access);
      Interfaces.C.Strings.Free (K.Exc_Info.Message);
      K.Exc_Info.Current_Exception := System.Null_Address;
   end Clear_Last_Exception;

   --------------------------------
   -- Create_Exception_Occurence --
   --------------------------------

   function Create_Exception_Occurence
     (Kind : Standard_Exception_Kind) return System.Address is
   begin
      case Kind is
         when Constraint_Error_Kind =>
            raise Constraint_Error;

         when Program_Error_Kind =>
            raise Program_Error;

         when Storage_Error_Kind =>
            raise Storage_Error;

         when Tasking_Error_Kind =>
            raise Tasking_Error;
      end case;
   exception
      when E : others =>
         return Exc_To_Addr (Save_Occurrence (E));
   end;

   function Create_Exception_Occurence_Message
     (Kind : Standard_Exception_Kind;
      Msg  : Polyglot.Ada.Strings.Polyglot_String) return System.Address
   is
      Ada_MSg : String (1 .. Positive (Polyglot.Ada.Strings.Length (Msg)))
      with Address => Msg.Data;
   begin
      case Kind is
         when Constraint_Error_Kind =>
            raise Constraint_Error with Ada_Msg;

         when Program_Error_Kind =>
            raise Program_Error with Ada_Msg;

         when Storage_Error_Kind =>
            raise Storage_Error with Ada_Msg;

         when Tasking_Error_Kind =>
            raise Tasking_Error with Ada_Msg;
      end case;
   exception
      when E : others =>
         return Exc_To_Addr (Save_Occurrence (E));
   end Create_Exception_Occurence_Message;

   ------------------------------
   -- Free_Exception_Occurence --
   ------------------------------

   procedure Free_Exception_Occurence (Addr : System.Address) is
      Occurence : Exception_Occurrence_Access := Addr_To_Exc (Addr);
   begin
      Exc_Free (Occurence);
   end Free_Exception_Occurence;

end Polyglot.Exceptions;
