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

--  This package provides an object-oriented, high-level interface to SQL
--  queries.
--  Instead of using static strings to write queries, this package allows you
--  to write queries using Ada function calls. It helps to ensure the syntactic
--  validity of the resulting queries, and, some packages can automatically be
--  generated from your database (see below), ensures type-safety and that the
--  query only references existing fields of the database. An example of such a
--  query is:
--
--     Q : SQL_Query :=
--           SQL_Select
--             (Fields => Table1.Field1 & Table2.Field2,
--              From   => Table1 & Table2,
--              Where  => Table1.Field3 = Table2.Field4);
--
--  This checks, among other things, that Field3 and Field4 are of the same
--  type.
--  This package itself does not provide a way to execute a query on a given
--  database. See GNATCOLL.SQL.Exec for such facilities.
--  As a result, this package is independent of any DBMS, and in fact does not
--  even require one to be installed on your system.
--
--  Automatic generation of database description
--  =============================================
--
--  This package depends on having types and subprograms that describe the
--  structure of the database. Writting such packages manually is tedious and
--  error prone. Instead, you should use the gnatcoll_db2ada tool to
--  automatically generate this description before each compilation. This
--  ensures that any SQL query in your application only references fields that
--  do exist in the database, and therefore helps detect at compilation time a
--  lot of possible errors that would otherwise only be detected at run time.
--
--  These generated packages should contain the following, for each table in
--  your database:
--
--     Ta_Table_Name : aliased constant String := "table_name";
--     package T_Table is
--        N_Field1 : aliased constant String := "field1";
--        N_Field2 : aliased constant String := "field2";
--        type Table (Instance : Cst_String_Access)
--           is new SQL_Table (Ta_Table_Name'Access, Instance) with
--        record
--           Field1 : SQL_Field_Integer
--              (Ta_Table_Name'Access, Instance, N_Field1'Access);
--           Field2 : SQL_Field_Integer
--              (Ta_Table_Name'Access, Instance, N_Field2'Access);
--        end record;
--
--         function FK (Self : Table; Foreign : SQL_Table'Class)
--            return SQL_Criteria;
--
--     end T_Table;
--
--  Finally, a default instance of the table that can be used in the queries:
--      Table : T_Table.Table (null);
--
--  FK is a subprogram to retrieve the foreign keys between two tables, to
--  simplify the writting of the sql queries. This is optional, and if you are
--  maintaining this package by hand you might not want to generate these.
--
--  The reason to use a package like the above is to avoid naming conflicts
--  between the functions generated for the fields, and the name of the
--  instances (in the example above, if another table was called Field1, and we
--  weren't using a package, we would have a naming conflict).
--
--  This way, a user might write a query with two instances of the table with
--  the following code (which uses the Ada2005 dotted notation, although this
--  isn't mandatory):
--      AI : T_Sales_Entity.Table := T_Sales_Entity.Table
--        (Rename (Sales_Entity, "foo"));
--      SQL_Select
--        (Fields => AI.Field1 & Action_Item.Field1,
--         From   => AI & Action_Item,
--         Where  => AI.FK (Action_Item))

with Ada.Calendar;
with Ada.Containers.Doubly_Linked_Lists;
with Ada.Containers.Indefinite_Doubly_Linked_Lists;
with Ada.Finalization;
with Ada.Strings.Unbounded;  use Ada.Strings.Unbounded;
with GNATCOLL.SQL_Impl;      use GNATCOLL.SQL_Impl;

package GNATCOLL.SQL is

   subtype Cst_String_Access is GNATCOLL.SQL_Impl.Cst_String_Access;

   ------------
   -- Tables --
   ------------

   type SQL_Table is abstract new GNATCOLL.SQL_Impl.SQL_Single_Table with private;

   overriding function To_String
     (Self : SQL_Table) return String;


private

   -------------------------
   -- Table and instances --
   -------------------------

   type SQL_Table is abstract new
     GNATCOLL.SQL_Impl.SQL_Single_Table with null record;

end GNATCOLL.SQL;
