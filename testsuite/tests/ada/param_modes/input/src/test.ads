package Test is

   type T is private;

   procedure In_Proc(A: in Integer; B: in T);
   procedure Out_Proc(A: out Integer; B: out T);
   procedure In_Out_Proc(A: in out Integer; B: in out T);

private

   type T is record
      V: Integer := 1;
   end record;

end Test;
