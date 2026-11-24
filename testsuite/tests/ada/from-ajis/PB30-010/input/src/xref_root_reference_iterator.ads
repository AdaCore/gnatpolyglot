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

package Xref_Root_Reference_Iterator is

   type Root_Reference_Iterator is tagged;

   type Root_Entity is abstract tagged null record;
   No_Root_Entity : aliased constant Root_Entity'Class;
   --  aliased is added to let AJIS make it accessible to GNATbench

   function Find_All_References
     (Entity                : Root_Entity;
      In_Scope              : Root_Entity'Class := No_Root_Entity)
      return Root_Reference_Iterator'Class is abstract;

   type Root_Reference_Iterator is abstract tagged null record;

   function At_End (Iter : Root_Reference_Iterator) return Boolean is abstract;
   --  Whether there are no more reference to return

   procedure Should_Be_Bound (P1 : Boolean; P2 : access Root_Entity'Class);

private

   type General_Entity is new Root_Entity with record
      Is_Fuzzy   : Boolean := False;  --  Whether this is a fuzzy match
   end record;

   overriding function Find_All_References
     (Entity                : General_Entity;
      In_Scope              : Root_Entity'Class := No_Root_Entity)
      return Root_Reference_Iterator'Class;

   No_General_Entity : aliased constant General_Entity :=
     (Is_Fuzzy   => False);

   No_Root_Entity : aliased constant Root_Entity'Class :=
     No_General_Entity;

   type Dummy_Reference_Iterator is new Root_Reference_Iterator
   with null record;

   overriding function At_End (Iter : Dummy_Reference_Iterator) return Boolean;
   --  Whether there are no more reference to return

   No_Reference_Iterator : constant Root_Reference_Iterator'Class :=
     Dummy_Reference_Iterator'(null record);

end Xref_Root_Reference_Iterator;
