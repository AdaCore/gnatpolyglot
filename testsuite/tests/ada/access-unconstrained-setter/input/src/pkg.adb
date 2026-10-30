package body Pkg is

   procedure Clobber is
      Junk : array (1 .. 1024) of Integer := (others => -1)
        with Volatile;
   begin
      Junk (1) := Junk (1024);
   end Clobber;

   function Img (First, Last : Integer) return String is
     (Integer'Image (First) & " .." & Integer'Image (Last));

   function V_Bounds return String is (Img (V'First, V'Last));
   function A_Bounds return String is (Img (A'First, A'Last));
   function C_Bounds (X : Rec) return String is (Img (X.C'First, X.C'Last));

end Pkg;
