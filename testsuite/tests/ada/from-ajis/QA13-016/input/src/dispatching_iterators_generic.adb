------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2017, AdaCore                          --
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

package body Dispatching_Iterators_Generic is

   --=========================================================================
   --  Exported Operations
   --=========================================================================
   ---------------------------------------------------------------------------
   --  Internal
   ---------------------------------------------------------------------------
   function Get_First
     (This : aliased Iterator)
      return Cursor
   is
     (This.M_Cursor);

   ---------------------------------------------------------------------------
   function Has_Element
     (This     : Iterator;
      Position : Cursor)
      return Boolean
   is
     (Position.Ref.Has_Element);

   ---------------------------------------------------------------------------
   function Next
     (This     : Iterator;
      Position : Cursor)
      return Cursor
   is
   begin
      Position.Ref.Next;
      --  Advancing changes the iterator, so the next cursor is its self-reference.
      return This.M_Cursor;
   end Next;

   ---------------------------------------------------------------------------
   function Get_Element
     (This     : Iterator;
      Position : Cursor)
      return Element_Type
   is
     (Position.Ref.Current_Element);

end Dispatching_Iterators_Generic;
