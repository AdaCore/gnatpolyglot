-- This example showcases the use access type in the generated interface, how
-- memory can be managed both manually and automatically if the target language
-- allows it.

with Ada.Unchecked_Deallocation;

package Example is

   type P is record
      I : Integer := 0;
   end record;

   type P_A is access all P;

   Acc : P_A := null;

   procedure Print;

   procedure Unchecked_Free is new Ada.Unchecked_Deallocation (P, P_A);

end Example;
