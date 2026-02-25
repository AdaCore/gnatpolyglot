--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Ada.Text_IO; use Ada.Text_IO;

package body Polyglot.Ada is

   procedure Adjust (Shadow : in out Shadow_Data) is
      Vtable : Minimal_Vtable
      with Address => Shadow.Vtable;
      pragma Import (Ada, Vtable);
   begin
      Shadow.Self :=
        Vtable.Clone_Dispatch
          (Shadow.Self, Shadow.Back_Ref_Converter (Shadow'Access));
      -- When cloning a shadow type, the object refererenced must be deep
      -- cloned to avoid possible double-free.
      Shadow.Self_Owner := Library;
      -- The cloned object is owned by the shadow object: As it could be never
      -- returned to the user, it must be freed on destruction of the shadow
      -- object.
   end Adjust;

   procedure Finalize (Shadow : in out Shadow_Data) is
      Vtable : Minimal_Vtable
      with Address => Shadow.Vtable;
      pragma Import (Ada, Vtable);
   begin
      if Shadow.Self_Owner = Library then
         Vtable.Free_Dispatch (Shadow.Self);
         -- The user object must be freed if the shadow object owns it.
      end if;
   end Finalize;

end Polyglot.Ada;
