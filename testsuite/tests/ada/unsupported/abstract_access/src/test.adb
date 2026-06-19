with Ada.Text_IO; use Ada.Text_IO;
with Ada.Tags;

package body Test is

   type Child is new Root with null record;
   type Child_Access is access all Child;

   procedure P1 (R : Root_Access) is null;

   procedure P2 (R : in out Root_Access) is
       Acc : Child_Access := new Child;
   begin
       R := Root_Access (Acc);
   end P2;

   function F1 return Root_Access is
   begin
       return null;
   end F1;

end Test;
