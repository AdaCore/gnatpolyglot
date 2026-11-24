with GNATCOLL.SQL; use GNATCOLL.SQL;
with GNATCOLL.SQL_Impl;
package Hello is

   type mon_type
      is new SQL_Table with null record;

   toto : mon_type;
   procedure Say (Something : String);


end Hello;
