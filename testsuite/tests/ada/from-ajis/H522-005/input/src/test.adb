with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   type Child_Of_Root is new Root with record
      null;
   end record;

   type Child_Of_Child_1 is new Child_1 with record
      null;
   end record;

   type Child_Of_Child_Of_Child_1 is new Child_Of_Child_1 with record
      null;
   end record;

   function Get_Object (Id : Integer) return Root_Access is
   begin
      case Id is
         when 1 =>
            return new Root;
         when 2 =>
            return new Child_1;
         when 3 =>
            return new Child_Of_Root;
         when 4 =>
            return new Child_Of_Child_1;
         when 5 =>
            return new Child_Of_Child_Of_Child_1;
         when others =>
            return null;
      end case;
   end Get_Object;

end Test;