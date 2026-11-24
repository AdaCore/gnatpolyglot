------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2018, AdaCore                          --
--                                                                          --
-- This is free software;  you can redistribute it  and/or modify it  under --
-- terms of the  GNU General Public License as published  by the Free Soft- --
-- ware  Foundation;  either version 3,  or (at your option) any later ver- --
-- sion.  This software is distributed in the hope  that it will be useful, --
-- but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHAN- --
-- TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public --
-- License for  more details.  You should have  received  a copy of the GNU --
-- General  Public  License  distributed  with  this  software;   see  file --
-- COPYING3.  If not, go to http://www.gnu.org/licenses for a complete copy --
-- of the license.                                                          --
------------------------------------------------------------------------------

with Ada.Containers.Doubly_Linked_Lists;
package Hello is

   type Root_Interface is interface;

   function Get_Root_Value (This : Root_Interface) return Integer is abstract;

   type Derived_Interface is interface and Root_Interface;

   function Get_Derived_Value
     (This : Derived_Interface) return Integer is abstract;

   function Get_Rpgi_Value
     (This : Derived_Interface) return Integer is abstract;

   function Get_Ada_Root return Root_Interface'Class;

   function Get_Ada_Derived return Derived_Interface'Class;

   procedure Call_Interfaces (Root : Root_Interface'Class;
                              Derived : Derived_Interface'Class);

   type Derived_Interface_Bis is interface and Derived_Interface;

   procedure Hello_World;

end Hello;
