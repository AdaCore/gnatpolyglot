package body Test is

   procedure Initialize (V : in out A_Record) is
   begin
      for J in V.Arr'Range loop
         V.Arr (J) := J;
      end loop;
   end Initialize;

   function Get_Sum (R : A_Record) return Integer is
      Total : Integer := 0;
   begin
      for J in R.Arr'Range loop
         Total := Total + R.Arr (J);
      end loop;

      return Total;
   end Get_Sum;

end Test;