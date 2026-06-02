--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Ada.Text_IO; use Ada.Text_IO;
with Ada.Exceptions; use Ada.Exceptions;
with GNATpolyglot.Exceptions;

package body GNATpolyglot.Ada is

   procedure Adjust (Shadow : in out Shadow_Data) is
      Vtable : Minimal_Vtable
      with Address => Shadow.Vtable;
      pragma Import (Ada, Vtable);
      Exc_Occ_A : Standard.Ada.Exceptions.Exception_Occurrence_Access;
      Exc_Occ : Standard.Ada.Exceptions.Exception_Occurrence;
   begin
      Shadow.Self :=
        Vtable.Clone_Dispatch
          (Shadow.Self_Data,
           Shadow.Self,
           Shadow.Back_Ref_Converter (Shadow'Access));
      -- When cloning a shadow type, the object refererenced must be deep
      -- cloned to avoid possible double-free.
      Shadow.Self_Owner := Library;
      -- The cloned object is owned by the shadow object: As it could be never
      -- returned to the user, it must be freed on destruction of the shadow
      -- object.
      Exc_Occ_A :=
        GNATpolyglot.Exceptions.Addr_To_Exc
          (GNATpolyglot.Get_Kernel.Exc_Info.Current_Exception);
      if Exc_Occ_A /= null then
         GNATpolyglot.Get_Kernel.Exc_Info.Current_Exception := System.Null_Address;
         Standard.Ada.Exceptions.Save_Occurrence (Exc_Occ, Exc_Occ_A.all);
         GNATpolyglot.Exceptions.Exc_Free (Exc_Occ_A);
         Standard.Ada.Exceptions.Reraise_Occurrence (Exc_Occ);
      end if;
   end Adjust;

   procedure Finalize (Shadow : in out Shadow_Data) is
      Vtable : Minimal_Vtable
      with Address => Shadow.Vtable;
      pragma Import (Ada, Vtable);
   begin
      if Shadow.Self_Owner = Library then
         Vtable.Free_Dispatch (Shadow.Self_Data, Shadow.Self);
         -- The user object must be freed if the shadow object owns it.
      end if;
      if Vtable.Free_Data /= null then
         Vtable.Free_Data (Shadow.Self_Data);
      end if;
   end Finalize;

end GNATpolyglot.Ada;
