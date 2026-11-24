------------------------------------------------------------------------------
--                             G N A T C O L L                              --
--                                                                          --
--                     Copyright (C) 2005-2012, AdaCore                     --
--                                                                          --
-- This library is free software;  you can redistribute it and/or modify it --
-- under terms of the  GNU General Public License  as published by the Free --
-- Software  Foundation;  either version 3,  or (at your  option) any later --
-- version. This library is distributed in the hope that it will be useful, --
-- but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHAN- --
-- TABILITY or FITNESS FOR A PARTICULAR PURPOSE.                            --
--                                                                          --
-- As a special exception under Section 7 of GPL version 3, you are granted --
-- additional permissions described in the GCC Runtime Library Exception,   --
-- version 3.1, as published by the Free Software Foundation.               --
--                                                                          --
-- You should have received a copy of the GNU General Public License and    --
-- a copy of the GCC Runtime Library Exception along with this program;     --
-- see the files COPYING3 and COPYING.RUNTIME respectively.  If not, see    --
-- <http://www.gnu.org/licenses/>.                                          --
--                                                                          --
------------------------------------------------------------------------------

with Ada.Calendar;
with Ada.Containers.Doubly_Linked_Lists;
with Ada.Containers.Indefinite_Doubly_Linked_Lists;
with Ada.Containers.Indefinite_Hashed_Sets;
with Ada.Finalization;

package GNATCOLL.SQL_Impl is

   type Cst_String_Access is access constant String;

   type SQL_Table_Or_List is abstract tagged private;
   --  Either a single table or a group of tables

   function To_String
     (Self : SQL_Table_Or_List)
      return String is abstract;
   --  Convert the table to a string

   type SQL_Single_Table is abstract new SQL_Table_Or_List with private;
   --  Any type of table, or result of join between several tables. Such a
   --  table can have fields

private

   type SQL_Table_Or_List is abstract tagged null record;

   type SQL_Single_Table is abstract new SQL_Table_Or_List with null record;
   --  instance name, might be null when this is the same name as the table.
   --  This isn't used for lists, but is used for all other types of tables
   --  (simple, left join, subqueries) so is put here for better sharing.

end GNATCOLL.SQL_Impl;
