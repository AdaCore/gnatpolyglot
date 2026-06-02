--
--  Copyright (C) 2025-2026, AdaCore
--  SPDX-License-Identifier: Apache-2.0
--

with Ada.Finalization; use Ada.Finalization;
with System;

package GNATpolyglot.Ada is

   type Self_Owner_Kind is (Library, User);

   type Shadow_Interface is limited interface;
   -- Interface implemented by every shadow types in generated bindings.

   procedure Set_Self_Owner
     (Shadow : out Shadow_Interface; Owner : Self_Owner_Kind)
   is abstract;
   -- Set whether the back-reference to the user value is owned by the library
   -- or the user.

   type Clone_Shadow_Type is
     access function
       (Self_Data : System.Address;
        Self      : System.Address;
        Data      : System.Address) return System.Address
   with Convention => C;

   type Free_Dispatch_Shadow_Type is
     access procedure (Self_Data : System.Address; Self : System.Address)
   with Convention => C;

   type Free_Shadow_Type is access procedure (Self : System.Address)
   with Convention => C;

   type Minimal_Vtable is record
      Clone_Dispatch : Clone_Shadow_Type;
      Free_Dispatch  : Free_Dispatch_Shadow_Type;
      Free_Data      : Free_Shadow_Type;
   end record
   with Convention => C;
   -- Minimal vtable that can be overlaid over any generated vtable.
   --
   -- It allows the bindings to call the user-side clone function or the user
   -- destructor when necessary, from Ada code.

   type Shadow_Data;
   type Back_Ref_Converter_Type is
     access function (Self : access Shadow_Data) return System.Address;

   type Shadow_Data is new Controlled with record
      Self               : System.Address;
      -- The reference to the user-side object
      Self_Data          : System.Address;
      -- Additional opaque data. This data is passed to dispatching functions
      -- called from the vtable. Its goal is to hold values meant to help with
      -- redispatching to the user implementations (e.g. hold a reference to
      -- a Java VM).
      -- If the `Free_Data` function of the minimal vtable is set, it is called
      -- on this object to free it.
      Vtable             : System.Address;
      -- The virtual table that holds pointers to dispatching functions
      Self_Owner         : Self_Owner_Kind;
      -- Whether ``Self`` is owned by the shadow object or not.
      Back_Ref_Converter : Back_Ref_Converter_Type;
      -- Callback to get the Shadow object from this Shadow_Data.
      -- Conceptually, this could be an overridable primitive of the
      -- Shadow_Data type. However, this would imply that each shadow type
      -- would store its own ``Shadow_Data`` as a `access Shadow_Data'Class`,
      -- adding yet an other pointer in the mix for the sake of using Ada's
      -- dynamic dispatch for a single subprogram.
   end record;
   -- Record holding information about shadow types.
   --
   -- Shadow types are types derivating from Ada tagged types, overriding all
   -- dispatching functions in order to allow dynamic dispatching from the user
   -- of the bindings. It holds a virtual table and a reference to the user
   -- object, allowing dispatching calls to dispatch to the user and possibly
   -- overriding Ada subprograms from an other language.

   overriding
   procedure Adjust (Shadow : in out Shadow_Data);

   overriding
   procedure Finalize (Shadow : in out Shadow_Data);

end GNATpolyglot.Ada;
