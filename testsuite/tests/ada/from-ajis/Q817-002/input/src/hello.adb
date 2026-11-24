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

pragma Ada_2012;
with Ada.Text_IO; use Ada.Text_IO;

package body Hello is

   type My_Root is new Root_Interface with record
      I : Integer;
   end record;

   --------------------
   -- Get_Root_Value --
   --------------------

   function Get_Root_Value (This : My_Root) return Integer
   is
   begin
      return This.I;
   end Get_Root_Value;

   My_Root_Interf : My_Root := (I => 1961);

   type My_Derived is new Derived_Interface with record
      I : Integer;
   end record;

   function Get_Derived_Value (This : My_Derived) return Integer;

   function Get_Root_Value (This : My_Derived) return Integer;

   --------------------
   -- Get_Rpgi_Value --
   --------------------

   overriding function Get_Rpgi_Value (This : My_Derived) return Integer;

   overriding function Get_Rpgi_Value (This : My_Derived) return Integer
   is
   begin
      return 0;
   end Get_Rpgi_Value;

   -----------------------
   -- Get_Derived_Value --
   -----------------------

   function Get_Derived_Value (This : My_Derived) return Integer
   is
   begin
      return 2 * Get_Root_Value (This);
   end Get_Derived_Value;

   --------------------
   -- Get_Root_Value --
   --------------------

   function Get_Root_Value (This : My_Derived) return Integer
   is
   begin
      return This.I;
   end Get_Root_Value;

   My_Derived_Interf : My_Derived := (I => 1995);

   ------------------
   -- Get_Ada_Root --
   ------------------

   function Get_Ada_Root return Root_Interface'Class is
   begin
      return My_Root_Interf;
   end Get_Ada_Root;

   ---------------------
   -- Get_Ada_Derived --
   ---------------------

   function Get_Ada_Derived return Derived_Interface'Class is
   begin
      return My_Derived_Interf;
   end Get_Ada_Derived;

   ---------------------
   -- Call_Interfaces --
   ---------------------

   procedure Call_Interfaces
     (Root    : Root_Interface'Class;
      Derived : Derived_Interface'Class)
   is
   begin
      Put_Line (Get_Root_Value (Root)'Image
                & " "
                & Get_Root_Value (Derived)'Image
                & Get_Derived_Value (Derived)'Image);
   end Call_Interfaces;

   -------------------
   --  Hello_World  --
   -------------------

   procedure Hello_World is
   begin
      Put_Line ("Hello ! Interfaces are : ");
      Call_Interfaces (My_Root_Interf, My_Derived_Interf);
   end Hello_World;

end Hello;
