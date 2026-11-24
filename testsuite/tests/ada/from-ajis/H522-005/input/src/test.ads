with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Root is tagged record
      null;
   end record;

   type Child_1 is new Root with record
      null;
   end record;

   type Root_Access is access all Root'Class;

   function Get_Object (Id : Integer) return Root_Access;

end Test;