------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2014, AdaCore                          --
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

with Ada.Containers.Indefinite_Hashed_Maps;
with Ada.Strings.Hash;

package Hello is

   type Root_Entity is abstract tagged null record;
   No_Root_Entity : aliased constant Root_Entity'Class;
   --  aliased is added to let AJIS make it accessible to GNATbench

   type General_Location is record
      Line   : Integer := 0;
   end record;

   type Lang_Specific_Database is interface;
   function Get_Entity
     (Db : Lang_Specific_Database;
      Name : String;
      Loc : General_Location) return Root_Entity'Class is abstract;

   package Lang_Specific_Databases_Maps
   is new Ada.Containers.Indefinite_Hashed_Maps
     (String,
      Lang_Specific_Database'Class,
      Hash => Ada.Strings.Hash, Equivalent_Keys => "=");

   type General_Xref_Database_Record is tagged record
      Lang_Specific_Databases : Lang_Specific_Databases_Maps.Map;
   end record;

   type General_Entity is new Root_Entity with record
      Line   : Integer := 0;
   end record;

   function Get_Entity
     (Db   : access General_Xref_Database_Record;
      Name : String;
      Loc  : General_Location) return Root_Entity'Class;

private

   No_General_Entity : aliased constant General_Entity := (Line => 1961);

   No_Root_Entity : aliased constant Root_Entity'Class :=
     No_General_Entity;

end Hello;
