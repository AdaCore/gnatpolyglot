with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Root is abstract tagged record
      null;
   end record;

   procedure P_1 (V : Root) is abstract;

   type Child_1 is new Root with record
      null;
   end record;

   procedure P_1 (V : Child_1);

   function Get_Direct_Root_Child return Root'Class;

   procedure Display_P1 (Dummy : Boolean; V : Root'Class);

end Test;
