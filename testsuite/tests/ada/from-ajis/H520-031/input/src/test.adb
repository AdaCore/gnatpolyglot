package body Test is

   procedure P_1
     (P1 : out Integer;
      P2 : out Boolean;
      P3 : out Float;
      P4 : out Enum)
   is
   begin
      P1 := 9;
      P2 := True;
      P3 := 16.2;
      P4 := B;
   end P_1;

   procedure P_2
     (P1 : in out Integer;
      P2 : in out Boolean;
      P3 : in out Float;
      P4 : in out Enum) is
   begin
      P1 := P1 + 1;
      P2 := not P2;
      P3 := P3 * 10.0;
      P4 := Enum'Succ (P4);
   end P_2;

end Test;
