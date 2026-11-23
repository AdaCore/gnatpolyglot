------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2016, AdaCore                          --
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

package Xref_Abstract_Entities_Cursor is

   type Abstract_Entities_Cursor is tagged;

   type Root_Entity is abstract tagged null record;
   No_Root_Entity : aliased constant Root_Entity'Class;
   --  aliased is added to let AJIS make it accessible to GNATbench

   function Get_All_Called_Entities
     (Entity : Root_Entity) return Abstract_Entities_Cursor'Class is abstract;
   --  Return all the entities that are found in the scope of Entity. This is
   --  not necessarily a subprogram call, but can be many things.
   --  All entities returned are unique. If you need to find the specific
   --  reference(s) to that entity, you'll need to search for the references in
   --  the right scope through the iterators above.

   type Abstract_Entities_Cursor is interface;
   function At_End
     (Iter : Abstract_Entities_Cursor) return Boolean is abstract;

   type Base_Entities_Cursor is new Abstract_Entities_Cursor with null record;

   overriding function At_End
     (Iter : Base_Entities_Cursor) return Boolean;

   procedure Should_Be_Bound (P1 : Boolean; P2 : access Root_Entity'Class);

private

   type General_Entity is new Root_Entity with record
      Is_Fuzzy   : Boolean := False;  --  Whether this is a fuzzy match
   end record;

   overriding function Get_All_Called_Entities
     (Entity : General_Entity) return Abstract_Entities_Cursor'Class;

   No_General_Entity : aliased constant General_Entity :=
     (Is_Fuzzy   => False);

   No_Root_Entity : aliased constant Root_Entity'Class :=
     No_General_Entity;

end Xref_Abstract_Entities_Cursor;
