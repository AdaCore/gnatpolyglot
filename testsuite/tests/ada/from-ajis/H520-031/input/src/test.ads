with Ada.Text_IO; use Ada.Text_IO;

package Test is

   type Enum is (A, B, C);

   procedure P_1
     (P1 : out Integer;
      P2 : out Boolean;
      P3 : out Float;
      P4 : out Enum);

   procedure P_2
     (P1 : in out Integer;
      P2 : in out Boolean;
      P3 : in out Float;
      P4 : in out Enum);

end Test;
